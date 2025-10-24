package com.sidspace.loven.home.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.mapper.toUserDomain
import com.sidspace.core.data.mapper.toUserSession
import com.sidspace.core.data.model.UserManager
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.core.domain.model.UserDomain
import com.sidspace.loven.home.data.data.listWords
import com.sidspace.loven.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userManager: UserManager,
    private val googleSignInClient: GoogleSignInClient,
    private val firestore: FirebaseFirestore
) : HomeRepository {

    override fun getBackgroundWords(): DomainResult<List<String>> {
        return DomainResult.Success(listWords)
    }

    override suspend fun getAccount(): DomainResult<UserDomain> {
        return if (userManager.user != null) {
            DomainResult.Success(userManager.user!!.toUserDomain())
        } else {
            val user = getUserFromFirebase()
            if (user.exists()) {
                userManager.initUser(user.toUserSession())
                DomainResult.Success(userManager.user!!.toUserDomain())
            } else {
                DomainResult.Error
            }
        }

    }

    @Suppress("TooGenericExceptionCaught")
    override fun signOut(): DomainResult<Unit> {
        return try {
            userManager.clearUser()
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            DomainResult.Error
        }
    }

    override fun observeLives(): Flow<Long> = userManager.lifeState

    override fun observeTimeToNextLive(): Flow<Long?> = userManager.timeUntilNextLife

    suspend fun getUserFromFirebase(): DocumentSnapshot {
        val user =
            firestore.collection(FirestoreCollections.USERS).document(firebaseAuth.currentUser!!.uid).get()
                .await()
        return user
    }
}
