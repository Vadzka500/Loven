package com.sidspace.loven.languages.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.languages.domain.usecase.GetLanguagesUseCase
import com.sidspace.loven.languages.presentation.mapper.toLanguageUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(private val getLanguagesUseCase: GetLanguagesUseCase) : ViewModel() {

    private val _state = MutableStateFlow(LanguageState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LanguageEffect>()
    val effect = _effect.asSharedFlow()


    init {
        getLanguages()
    }


    fun getLanguages() {
        viewModelScope.launch {
            when (val data = getLanguagesUseCase()) {
                DomainResult.Error -> {
                    _state.update { it.copy(listLanguages = ResultUi.Error) }
                }

                is DomainResult.Success -> {
                    _state.update { it.copy(listLanguages = ResultUi.Success(data.data.toLanguageUi())) }
                }
            }
        }
    }

}
