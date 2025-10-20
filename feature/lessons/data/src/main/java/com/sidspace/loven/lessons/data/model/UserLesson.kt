package com.sidspace.loven.lessons.data.model

data class UserLesson(
    val id: String?,
    val idLesson: String,
    val starCount: Long
)

data class UserLessonFirebase(
    val idLesson: String,
    val starCount: Long
)
