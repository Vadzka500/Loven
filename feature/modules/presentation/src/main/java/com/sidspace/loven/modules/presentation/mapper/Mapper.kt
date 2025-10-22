package com.sidspace.loven.modules.presentation.mapper

import com.sidspace.loven.modules.domain.model.ModuleDomain
import com.sidspace.loven.modules.presentation.model.ModuleUi

fun ModuleDomain.toModuleUi(): ModuleUi {
    return ModuleUi(
        this.id,
        this.idLanguage,
        this.name,
        this.imageUrl,
        this.description,
        this.lessonsCount,
        this.starsToEnable,
        this.usersStars,
        this.maxStars,
        this.isEnableModule,
        this.starsToUnlock,
        this.isCompleted
    )
}
