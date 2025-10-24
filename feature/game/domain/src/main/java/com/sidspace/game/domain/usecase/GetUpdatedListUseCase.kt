package com.sidspace.game.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.game.GameManager
import com.sidspace.game.domain.model.GameWords
import javax.inject.Inject

class GetUpdatedListUseCase @Inject constructor(private val gameManager: GameManager) {
    operator fun invoke(list: List<Pair<String, String>>): DomainResult<GameWords>? =
        gameManager.getGame()?.let {
            DomainResult.Success(it.getUpdatedList(list))
        } ?: DomainResult.Error
}
