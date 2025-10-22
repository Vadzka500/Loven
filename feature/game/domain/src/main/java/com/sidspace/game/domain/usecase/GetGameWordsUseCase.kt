package com.sidspace.game.domain.usecase

import com.sidspace.core.domain.model.DomainResult
import com.sidspace.core.domain.model.GameModeDomain
import com.sidspace.game.domain.model.GameLifeDomain
import com.sidspace.game.domain.model.GameStateDomain
import com.sidspace.game.domain.model.GameWords
import com.sidspace.game.domain.model.Word
import com.sidspace.game.domain.repository.GameRepository
import com.sidspace.loven.utils.GameConstants
import com.sidspace.loven.utils.isEnglishOnly
import javax.inject.Inject

private lateinit var game: Game

class GetGameWordsUseCase @Inject constructor(private val repository: GameRepository) {

    suspend operator fun invoke(
        languageId: String,
        moduleId: String,
        lessonId: String
    ): DomainResult<GameWords> {
        val data = repository.getGameWords(languageId, moduleId, lessonId)
        if (data is DomainResult.Success) game =
            Game(data.data.words.shuffled(), languageId, moduleId, lessonId, data.data.type)
        return DomainResult.Success(game.getInitialWords())
    }

}

class IsCorrectWordUseCase @Inject constructor(private val repository: GameRepository) {
    suspend operator fun invoke(wordRu: String, wordTranslate: String): GameStateDomain {

        val isCorrect = game.checkWord(wordRu, wordTranslate)
        if (!isCorrect) {
            return when (repository.inCorrectWords()) {
                GameLifeDomain.ContinueGame -> GameStateDomain.IsInCorrect
                GameLifeDomain.EndGame -> GameStateDomain.EndLives
            }
        }
        return GameStateDomain.IsCorrect
    }
}

/*class GetUpdatedListUseCase @Inject constructor() {
    operator fun invoke(wordRu: String, wordTranslate: String): DomainResult<GameWords>? =
        DomainResult.Success(game.getUpdatedList(wordRu, wordTranslate))
}*/

class GetUpdatedListUseCase @Inject constructor() {
    operator fun invoke(list: List<Pair<String, String>>): DomainResult<GameWords>? =
        DomainResult.Success(game.getUpdatedList(list))
}

/*class ClearSelectWordUseCase @Inject constructor() {
    operator fun invoke(wordRu: String, wordTranslate: String): DomainResult<GameWords>? =
        DomainResult.Success(game.getUpdatedList(wordRu, wordTranslate))
}*/

class Game(
    private val startList: List<Word>,
    private val idLanguage: String,
    private val idModule: String,
    private val idLesson: String,
    private val gameMode: GameModeDomain
) {

    private var initialPoolSize = GameConstants.INITIAL_WORDS_SIZE

    private var errorCount = 0

    private var iterator = initialPoolSize


    lateinit var currentList: GameWords

    fun getErrorCount(): Int {
        return errorCount
    }

    fun getInitialWords(): GameWords {

        var listRu = mutableListOf<String?>()
        var listTranslate = mutableListOf<String?>()

        if (gameMode == GameModeDomain.SWAP) {
            startList.take(initialPoolSize).forEachIndexed { index, words ->

                if (index % 2 == 1) {
                    listRu.add(words.wordRu)
                    listTranslate.add(words.wordTranslate)
                } else {
                    listRu.add(words.wordTranslate)
                    listTranslate.add(words.wordRu)
                }

            }

            listRu = listRu.shuffled().toMutableList()
            listTranslate = listTranslate.shuffled().toMutableList()

            currentList = GameWords(idLanguage, idModule, idLesson, listRu, listTranslate, gameMode)

        } else {
            currentList = startList.take(initialPoolSize).let { words ->

                val listRu = words.map { it.wordRu }.shuffled().toMutableList()
                val listTranslate = words.map { it.wordTranslate }.shuffled().toMutableList()

                GameWords(idLanguage, idModule, idLesson, listRu, listTranslate, gameMode)
            }

        }
        return currentList

    }

    fun checkWord(wordRu: String, wordTranslate: String): Boolean {
        println("list = " + startList)
        println("get = " + startList.find { it.wordRu == wordRu })
        println("ru = " + wordRu)
        println("trans = " + wordTranslate)

        val isCorrect =
            if (isEnglishOnly(wordTranslate)) startList.find { it.wordRu == wordRu }?.wordTranslate == wordTranslate
            else startList.find { it.wordRu == wordTranslate }?.wordTranslate == wordRu
        if (!isCorrect) errorCount++
        return isCorrect
    }

    fun clearWord(wordRu: String, wordTranslate: String): GameWords {


        currentList.let {

            val newRuWords = currentList.listRuWords.toMutableList()
            val newTranslateWords = currentList.listTranslateWords.toMutableList()

            newRuWords[newRuWords.indexOf(wordRu)] = ""
            newTranslateWords[newTranslateWords.indexOf(wordTranslate)] = ""
            currentList = currentList.copy(
                listRuWords = newRuWords,
                listTranslateWords = newTranslateWords
            )
            return currentList

        }


    }

    fun getNextWord(): Word? {
        if (iterator >= startList.size) return null
        return startList[iterator++]
    }

    fun getUpdatedList(list: List<Pair<String, String>>): GameWords {

        if (iterator >= startList.size) {

            val listRu = currentList.listRuWords.toMutableList()
            val listTranslate = currentList.listTranslateWords.toMutableList()

            list.forEachIndexed { index, _ ->
                listRu[listRu.indexOf(list[index].first)] = null
                listTranslate[listTranslate.indexOf(list[index].second)] = null
            }

            currentList = currentList.copy(
                listRuWords = listRu,
                listTranslateWords = listTranslate
            )

            return currentList

        }

        if (list.size > 1) {

            val l = List(list.size) {
                getNextWord()
            }

            val listRu = l.map { it?.wordRu }.toMutableList()
            val listTranslate = l.map { it?.wordTranslate }.toMutableList()



            if (gameMode == GameModeDomain.SWAP) {
                list.forEachIndexed { index, item ->
                    if (isEnglishOnly(item.first)) {
                        val word = listRu[index]
                        listRu[index] = listTranslate[index]
                        listTranslate[index] = word
                    }
                }
            }


            listRu.reversed().forEachIndexed { index, word ->
                val newRuWords = currentList.listRuWords.toMutableList()
                val newTranslateWords = currentList.listTranslateWords.toMutableList()


                newRuWords[newRuWords.indexOf(list[index].first)] = word
                newTranslateWords[newTranslateWords.indexOf(list[index].second)] = listTranslate[index]


                currentList = currentList.copy(
                    listRuWords = newRuWords,
                    listTranslateWords = newTranslateWords
                )
            }

            return currentList

        } else {
            val newWord = getNextWord()

            newWord?.let {

                val newRuWords = currentList.listRuWords.toMutableList()
                val newTranslateWords = currentList.listTranslateWords.toMutableList()

                if (gameMode == GameModeDomain.SWAP) {
                    if (isEnglishOnly(list[0].first)) {
                        newRuWords[newRuWords.indexOf(list[0].first)] = newWord.wordRu
                        newTranslateWords[newTranslateWords.indexOf(list[0].second)] = newWord.wordTranslate

                    } else {
                        newRuWords[newRuWords.indexOf(list[0].first)] = newWord.wordTranslate
                        newTranslateWords[newTranslateWords.indexOf(list[0].second)] = newWord.wordRu
                    }
                } else {
                    newRuWords[newRuWords.indexOf(list[0].first)] = newWord.wordRu
                    newTranslateWords[newTranslateWords.indexOf(list[0].second)] = newWord.wordTranslate
                }


                currentList = currentList.copy(
                    listRuWords = newRuWords,
                    listTranslateWords = newTranslateWords
                )
                return currentList

            }
        }

        return currentList
    }

}
