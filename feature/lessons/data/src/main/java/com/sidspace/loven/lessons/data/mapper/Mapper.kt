package com.sidspace.loven.lessons.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.loven.lessons.data.model.UserLesson

import com.sidspace.loven.lessons.domain.model.LessonDomain

fun List<DocumentSnapshot>.toLessonsDomain(
    idLanguage: String,
    idModule: String,
    list: List<UserLesson>
): List<LessonDomain> {
    return this.mapIndexed { index, item ->
        LessonDomain(
            item.id,
            idLanguage,
            idModule,
            GameModeDomain.valueOf(item.get("type").toString()),
            if (list.size > index) list[index].starCount else null
        )
    }
}

fun QuerySnapshot.toUserLessons(): List<UserLesson> {
    return this.map { item ->
        UserLesson(
            id = item.id,
            idLesson = item.data["idLesson"].toString(),
            starCount = item.data["starCount"] as Long
        )
    }
}
