package com.sidspace.loven.game.data.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.sidspace.game.domain.model.Game
import com.sidspace.game.domain.model.Word

fun QuerySnapshot.toDomain(): Game {
    return Game(this.map { item ->
        Word(
            item.data.values.take(1).toString(),
            item.data.values.take(2).toString()
        )
    })
}
