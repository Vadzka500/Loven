package com.sidspace.loven.core.presentation.mapper

import com.sidspace.core.domain.model.UserDomain
import com.sidspace.loven.core.presentation.model.UserUi

fun UserDomain.toUserUi(): UserUi {
    return UserUi(
        id = id,
        name = name,
        photo = photoUrl,
        email = email,
        lifeCount = lifeCount
    )
}
