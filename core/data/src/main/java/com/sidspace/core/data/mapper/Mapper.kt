package com.sidspace.core.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.sidspace.core.data.model.UserSession
import com.sidspace.core.domain.model.UserDomain

fun UserSession.toUserDomain(): UserDomain {
    return UserDomain(
        id = id,
        name = name,
        photoUrl = photoUrl,
        email = email,
        lifeCount = lifeCount
    )
}

fun DocumentSnapshot.toUserSession(): UserSession {
    val timestamp = data?.get("lastLifeTimestamp") as? Timestamp
    return UserSession(
        id = id,
        name = data?.get("name").toString(),
        photoUrl = data?.get("email").toString(),
        email = data?.get("photoUrl").toString(),
        lifeCount = data?.get("lifeCount") as Long,
        lastLifeTimestamp = timestamp?.toDate()
    )
}
