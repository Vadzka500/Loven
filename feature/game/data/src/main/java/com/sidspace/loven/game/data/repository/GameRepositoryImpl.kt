package com.sidspace.loven.game.data.repository

import com.google.firebase.firestore.FirebaseFirestore
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
        languageId: String, moduleId: String, lessonId: String, starCount: Int
    ): DomainResult<Unit> {
        val userSnapshot =
            firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id).get().await()

        if (!userSnapshot.exists()) return DomainResult.Error

        val lessonsCollection = userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(languageId)
            .collection(FirestoreCollections.MODULES).document(moduleId).collection(FirestoreCollections.LESSON)


        val querySnapshot = lessonsCollection.whereEqualTo("idLesson", lessonId).get().await()

        if ((querySnapshot.documents.first().get("starCount") as Long) < starCount) {
            querySnapshot.documents.first().reference.update("starCount", starCount).await()
        }

        return DomainResult.Success(Unit)
    }

    override suspend fun inCorrectWords(): GameLifeDomain {

        val isContinueGame = userManager.minusLife()

        val userSnapshot = firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id)

        userSnapshot.update("lifeCount", userManager.user!!.lifeCount)

        if (isContinueGame) return GameLifeDomain.ContinueGame
        else return GameLifeDomain.EndGame
    }


}
