package com.sidspace.loven.lessons.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sidspace.core.data.model.UserManager
import com.sidspace.core.data.model.UserModule
import com.sidspace.core.data.utils.FirestoreCollections
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.lessons.data.mapper.toLessonsDomain

import com.sidspace.loven.lessons.data.mapper.toUserLessons
import com.sidspace.loven.lessons.data.model.UserLesson
import com.sidspace.loven.lessons.data.model.UserLessonFirebase

import com.sidspace.loven.lessons.domain.model.LessonDomain
import com.sidspace.loven.lessons.domain.repository.LessonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class LessonsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore, private val userManager: UserManager
) : LessonsRepository {

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun getLessons(idLanguage: String, idModule: String): DomainResult<List<LessonDomain>> {


        return try {

            val lessons = firestore.collection(FirestoreCollections.LANGUAGE).document(idLanguage)
                .collection(FirestoreCollections.MODULES).document(idModule).collection(FirestoreCollections.LESSON)
                .get().await()

            val listUserLessons = getUserLessons(idLanguage, idModule).toMutableList()
            if (listUserLessons.isEmpty()) {
                addLesson(idLanguage, idModule, UserLesson(null, lessons.documents.first().id, 0))?.apply {
                    listUserLessons.add(UserLesson(this, lessons.documents.first().id, 0))
                }

            } else if (listUserLessons.maxByOrNull {
                    it.idLesson.replace("lesson ", "").toInt()
                }!!.starCount != 0L && listUserLessons.size < lessons.documents.size) {

                addLesson(
                    idLanguage, idModule, UserLesson(
                        null, lessons.documents.sortedBy {
                            it.get("name").toString().replace("lesson ", "").toInt()
                        }[listUserLessons.size].id, 0
                    )
                )?.apply {
                    println("add item")
                    listUserLessons.add(
                        UserLesson(
                            this, lessons.documents.sortedBy {
                                it.get("name").toString().replace("lesson ", "").toInt()
                            }[listUserLessons.size].id, 0
                        )
                    )
                }
            }


            DomainResult.Success(lessons.documents.sortedBy {
                it.get("name").toString().replace("lesson ", "").toInt()
            }.toLessonsDomain(idLanguage, idModule, listUserLessons.sortedBy {
                it.idLesson.replace("lesson ", "").toInt()
            }))
        } catch (e: Exception) {

            DomainResult.Error
        }


    }

    override fun getLivesCount(): Flow<Long> {
        return userManager.lifeState
    }

    private suspend fun getUserLessons(
        idLanguage: String, idModule: String
    ): List<UserLesson> {
        val userSnapshot =
            firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id).get().await()

        if (!userSnapshot.exists()) return emptyList()

        val lessonsSnapshot =
            userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(idLanguage)
                .collection(FirestoreCollections.MODULES).document(idModule).collection(FirestoreCollections.LESSON)
                .get().await()

        println("lesson = " + lessonsSnapshot.documents.toString())

        return lessonsSnapshot.toUserLessons()
    }

    private suspend fun addLesson(
        idLanguage: String, idModule: String, lesson: UserLesson
    ): String? {
        val userSnapshot =
            firestore.collection(FirestoreCollections.USERS).document(userManager.user!!.id).get().await()

        if (!userSnapshot.exists()) return null


        val moduleShapshot = userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(idLanguage)
            .collection(FirestoreCollections.MODULES).document(idModule).get().await()

        if (!moduleShapshot.exists()) {
            userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(idLanguage)
                .collection(FirestoreCollections.MODULES).document(idModule)
                .set(UserModule())
                .await()
        }

        val ref =
            userSnapshot.reference.collection(FirestoreCollections.LANGUAGE).document(idLanguage)
                .collection(FirestoreCollections.MODULES).document(idModule).collection(FirestoreCollections.LESSON)
                .add(UserLessonFirebase(lesson.idLesson, lesson.starCount)).await()

        println("✅ Lesson added with ID = ${ref.id}")
        return ref.id
    }
}
