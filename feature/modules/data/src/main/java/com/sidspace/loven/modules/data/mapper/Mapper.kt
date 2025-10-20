package com.sidspace.loven.modules.data.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.loven.modules.domain.model.ModuleDomain

fun QuerySnapshot.toDomain(idLanguage: String): List<ModuleDomain> {
    return this.map { item ->
        ModuleDomain(
            id = item.id,
            idLanguage = idLanguage,
            name = item.data["name"].toString(),
            imageUrl = item.data["imageUrl"].toString(),
            description = item.data["description"].toString(),
        )
    }
}
