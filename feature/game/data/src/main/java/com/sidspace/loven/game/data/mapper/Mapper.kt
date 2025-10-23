package com.sidspace.loven.game.data.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.game.domain.model.GameDomain
import com.sidspace.game.domain.model.Word

fun QuerySnapshot.toDomain(type: String): GameDomain {
    return GameDomain(this.map { item ->
        Word(
            item.data.values.elementAt(1).toString().lowercase(),
            item.data.values.elementAt(0).toString().lowercase()
        )
    }, type = GameModeDomain.valueOf(type))
}
