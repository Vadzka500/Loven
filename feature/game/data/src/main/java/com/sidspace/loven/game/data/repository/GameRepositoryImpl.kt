package com.sidspace.loven.game.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.core.data.model.UserManager
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.model.GameDomain
import com.sidspace.game.domain.model.GameLifeDomain
import com.sidspace.game.domain.repository.GameRepository
import com.sidspace.loven.game.data.mapper.toDomain
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore, private val userManager: UserManager
) : GameRepository {
    override suspend fun getGameWords(
        languageId: String, moduleId: String, lessonId: String
    ): DomainResult<GameDomain> {


        val lesson = firestore.collection(FirestoreCollections.LANGUAGE).document(languageId)
            .collection(FirestoreCollections.MODULES).document(moduleId).collection(FirestoreCollections.LESSON)
            .document(lessonId).get().await()

        if (lesson.exists()) {
            val words = lesson.reference.collection(FirestoreCollections.WORDS).get().await()

            if (!words.isEmpty) {
                return DomainResult.Success(words.toDomain(lesson.get("type").toString()))
            }
        }

        return DomainResult.Error
    }

    override suspend fun saveLesson(
        languageId: String, moduleId: String, lessonId: String, starCount: Int, isLastLesson: Boolean
    ): DomainResult<Unit> {

        val userSnapshot = getUser()

        if (!userSnapshot.exists()) return DomainResult.Error

        if (!isLastLesson) {
            val querySnapshot = getLesson(userSnapshot, languageId, moduleId, lessonId)

            val starDiff = starCount - (querySnapshot.documents.first().get("starCount") as Long)

            if (starDiff > 0) {
                querySnapshot.documents.first().reference.update("starCount", starCount).await()
                updateModule(userSnapshot, languageId, moduleId, starDiff)
            }
        } else {
            setCompleteModule(userSnapshot, languageId, moduleId)
        }

        return DomainResult.Success(Unit)
    }

    suspend fun getUser(): DocumentSnapshot {
        return firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id).get().await()
    }

    suspend fun setCompleteModule(
        userSnapshot: DocumentSnapshot,
        languageId: String,
        moduleId: String
    ) {
        userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(languageId)
            .collection(FirestoreCollections.MODULES).document(moduleId).update("isCompleted", true).await()
    }

    suspend fun updateModule(
        userSnapshot: DocumentSnapshot,
        languageId: String,
        moduleId: String,
        starDiff: Long
    ) {
        val module = userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(languageId)
            .collection(FirestoreCollections.MODULES).document(moduleId).get().await()

        val data = (module.get("starsCount") as Long + starDiff)

        userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(languageId)
            .collection(FirestoreCollections.MODULES).document(moduleId).update("starsCount", data).await()
    }

    suspend fun getLesson(
        userSnapshot: DocumentSnapshot,
        languageId: String,
        moduleId: String,
        lessonId: String,
    ): QuerySnapshot {
        val lessonsCollection = userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(languageId)
            .collection(FirestoreCollections.MODULES).document(moduleId).collection(FirestoreCollections.LESSON)

        return lessonsCollection.whereEqualTo("idLesson", lessonId).get().await()
    }

    override suspend fun inCorrectWords(): GameLifeDomain {

        val isContinueGame = userManager.minusLife()

        val userSnapshot = firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id)

        userSnapshot.update("lifeCount", userManager.user!!.lifeCount)

        if (isContinueGame) return GameLifeDomain.ContinueGame
        else return GameLifeDomain.EndGame
    }


}
