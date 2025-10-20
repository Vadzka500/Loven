package com.sidspace.core.data.model


import android.os.CountDownTimer
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.loven.utils.GameConstants.LIVES_MAX_COUNT
import com.sidspace.loven.utils.GameConstants.RESTORE_INTERVAL_MS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(private val firestore: FirebaseFirestore) {

    var user: UserSession? = null

    companion object {
        private const val CHECK_INTERVAL_MS = 1_000L
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _lifeState = MutableStateFlow(0L)
    val lifeState = _lifeState.asStateFlow()

    private val _timeUntilNextLife = MutableStateFlow<Long?>(null)
    val timeUntilNextLife = _timeUntilNextLife.asStateFlow()


    private var countDownTimer: CountDownTimer? = null


    fun initUser(userSession: UserSession) {
        user = userSession
        checkLives()
        _lifeState.value = userSession.lifeCount

    }

    fun checkLives() {
        val nowTime = Timestamp.now()
        val elapsed = nowTime.toDate().time - user?.lastLifeTimestamp!!.time

        val restoredLives = elapsed / RESTORE_INTERVAL_MS

        user!!.lifeCount = minOf(LIVES_MAX_COUNT.toLong(), user!!.lifeCount + restoredLives)

        if (restoredLives > 0) {
            scope.launch {
                updateLives()
                updateTimeStamp(nowTime)
            }

        }

        if (minOf(LIVES_MAX_COUNT.toLong(), user!!.lifeCount) < LIVES_MAX_COUNT) {
            val timeLeft = RESTORE_INTERVAL_MS - (elapsed % RESTORE_INTERVAL_MS)
            startTimer(timeLeft)
        }
    }

    fun minusLife(): Boolean {
        user!!.lifeCount--
        _lifeState.update { user?.lifeCount ?: 0 }
        if (user!!.lifeCount < LIVES_MAX_COUNT) {
            startTimer()
        }
        return user!!.lifeCount > 0
    }

    fun plusLife() {
        user!!.lifeCount = minOf(LIVES_MAX_COUNT.toLong(), user!!.lifeCount + 1)

        _lifeState.update { user?.lifeCount ?: 0 }
        if (minOf(LIVES_MAX_COUNT, user!!.lifeCount.toInt()) == LIVES_MAX_COUNT) {
            countDownTimer?.cancel()
        }
        scope.launch {
            updateLives()
        }

    }

    fun clearUser() {
        user = null
        countDownTimer?.cancel()
        countDownTimer = null
    }


    fun startTimer(totalTime: Long = RESTORE_INTERVAL_MS) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(totalTime, CHECK_INTERVAL_MS) {


            override fun onTick(millisUntilFinished: Long) {

                _timeUntilNextLife.value = millisUntilFinished
            }

            override fun onFinish() {
                val lives = _lifeState.value
                val newLives = lives + 1
                _lifeState.value = newLives
                user?.lifeCount = newLives

                _timeUntilNextLife.value = RESTORE_INTERVAL_MS
                val firebaseTime = Timestamp.now()

                scope.launch {
                    updateLives()
                    updateTimeStamp(firebaseTime)
                }


                if (_lifeState.value < LIVES_MAX_COUNT) {
                    startTimer()
                } else {
                    _timeUntilNextLife.value = null
                    countDownTimer = null
                }

            }
        }.start()
    }

    suspend fun updateLives() {
        firestore.collection(FirestoreCollections.USERS).document(user!!.id).update("lifeCount", user!!.lifeCount)
            .await()
    }

    suspend fun updateTimeStamp(timestamp: Timestamp) {
        firestore.collection(FirestoreCollections.USERS).document(user!!.id)
            .update("lastLifeTimestamp", timestamp).await()
    }

}
