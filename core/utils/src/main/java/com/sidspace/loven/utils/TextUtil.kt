package com.sidspace.loven.utils

fun isEnglishOnly(text: String?): Boolean {
    return text?.matches(Regex("^[A-Za-z]+$")) ?: false
}
