package com.sidspace.loven.languages.data.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.loven.languages.domain.model.LanguageDomain


fun QuerySnapshot.toLanguagesDomain(): List<LanguageDomain> {
    return this.map { item ->
        LanguageDomain(
            id = item.id,
            nameLanguage = item.data["name"].toString(),
            imageUrl = item.data["imageUrl"].toString(),
            isEnable = item.data["isEnable"] as Boolean,
            position = item.data["position"] as Long
        )
    }.sortedBy { it.position }
}
