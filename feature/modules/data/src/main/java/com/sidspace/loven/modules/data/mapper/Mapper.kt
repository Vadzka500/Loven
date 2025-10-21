package com.sidspace.loven.modules.data.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.loven.modules.domain.model.ModuleDomain

fun QuerySnapshot.toDomain(idLanguage: String, listStarsCount: Map<String, Long>): List<ModuleDomain> {
    return this.sortedBy { it.data["name"].toString().replace("Модуль ", "").toLong() }.mapIndexed { index, item ->
        ModuleDomain(
            id = item.id,
            idLanguage = idLanguage,
            name = item.data["name"].toString(),
            imageUrl = item.data["imageUrl"].toString(),
            description = item.data["description"].toString(),
            lessonsCount = item.data["lessonsCount"] as Long,
            starsToEnable = item.data["starsToEnable"] as Long,
            usersStars = listStarsCount.getOrDefault(item.id, 0),
            isEnableModule = listStarsCount.values.sum() >= item.data["starsToEnable"] as Long,
            starsToUnlock = item.data["starsToEnable"] as Long - listStarsCount.values.sum(),
            maxStars = ((item.data["lessonsCount"] as Long - 1) * 3)
        )
    }
}
