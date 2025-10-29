package com.sidspace.loven.authorization.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.mapper.toUserSession
import com.sidspace.core.data.model.UserManager
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult

import com.sidspace.loven.authorization.domain.model.AuthDomainResult
import com.sidspace.loven.authorization.domain.repository.AuthorizationRepository
import com.sidspace.loven.utils.GameConstants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userManager: UserManager,
    private val firestore: FirebaseFirestore
) :
    AuthorizationRepository {

    override suspend fun checkAccount(): AuthDomainResult {
        return if (firebaseAuth.currentUser != null) {
            AuthDomainResult.Authorized
        } else {
            AuthDomainResult.Unauthorized
        }
    }

    override suspend fun saveAccount(): DomainResult<Unit> {
        return if (firebaseAuth.currentUser != null) {

            var user = getUserFromFirebase()

            if (!user.exists()) {

                val data = hashMapOf(
                    "name" to firebaseAuth.currentUser!!.displayName,
                    "email" to firebaseAuth.currentUser!!.email,
                    "photoUrl" to firebaseAuth.currentUser!!.photoUrl,
                    "lifeCount" to GameConstants.LIVES_MAX_COUNT,
                    "lastLifeTimestamp" to FieldValue.serverTimestamp()
                )

                addUser(firebaseAuth.currentUser!!.uid, data)

                user = getUserFromFirebase()

                userManager.initUser(
                    user.toUserSession()
                )

            } else {
                userManager.initUser(
                    user.toUserSession()
                )
            }


            DomainResult.Success(Unit)
        } else {
            DomainResult.Error
        }
    }

    suspend fun getUserFromFirebase(): DocumentSnapshot {
        val user =
            firestore.collection(FirestoreCollections.USERS).document(firebaseAuth.currentUser!!.uid).get()
                .await()
        return user
    }

    suspend fun addUser(uid: String, data: HashMap<String, Any?>) {
        firestore.collection(FirestoreCollections.USERS)
            .document(uid)
            .set(data)
            .await()
    }
}
