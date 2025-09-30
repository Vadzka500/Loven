package com.sidspace.loven.game.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.domain.model.Game
import com.sidspace.game.domain.repository.GameRepository
import com.sidspace.loven.game.data.mapper.toDomain
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GameRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore): GameRepository  {
    override suspend fun getGameWords(languageId: String, moduleId: String, lessonId: String): DomainResult<Game> {
        return suspendCoroutine { cont ->
            firestore.collection(FirestoreCollections.LANGUAGE).document(languageId)
                .collection(FirestoreCollections.MODULES).document(moduleId)
                .collection(FirestoreCollections.LESSON).document(lessonId).
                collection(FirestoreCollections.WORDS).get().addOnSuccessListener { result ->


                cont.resume(DomainResult.Success(result.toDomain()))
            }.addOnFailureListener { error ->
                cont.resume(DomainResult.Error)
            }

        }
    }
}
