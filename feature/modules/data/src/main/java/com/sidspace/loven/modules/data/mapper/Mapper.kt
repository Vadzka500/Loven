package com.sidspace.loven.modules.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.loven.modules.data.model.UserModule
import com.sidspace.loven.modules.domain.model.ModuleDomain

fun QuerySnapshot.toDomain(idLanguage: String, listStarsCount: Map<String, UserModule>): List<ModuleDomain> {
    return this.sortedBy { it.data["name"].toString().replace("Модуль ", "").toLong() }.mapIndexed { index, item ->
        ModuleDomain(
            id = item.id,
            idLanguage = idLanguage,
            name = item.data["name"].toString(),
            imageUrl = item.data["imageUrl"].toString(),
            description = item.data["description"].toString(),
            lessonsCount = item.data["lessonsCount"] as Long,
            starsToEnable = item.data["starsToEnable"] as Long,
            usersStars = listStarsCount.getOrDefault(item.id, null)?.starsCount ?: 0,
            isEnableModule = listStarsCount.values.sumOf { it.starsCount } >= item.data["starsToEnable"] as Long,
            starsToUnlock = item.data["starsToEnable"] as Long - listStarsCount.values.sumOf { it.starsCount },
            maxStars = ((item.data["lessonsCount"] as Long - 1) * 3),
            isCompleted = listStarsCount.getOrDefault(item.id, null)?.isCompleted ?: false
        )
    }
}

fun DocumentSnapshot.toUserModule(): UserModule {
    return UserModule(
        this.get("starsCount") as Long,
        this.get("isCompleted") as Boolean
    )
}
