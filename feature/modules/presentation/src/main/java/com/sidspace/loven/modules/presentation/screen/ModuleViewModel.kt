package com.sidspace.loven.modules.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidspace.core.domain.model.DomainResult
import com.sidspace.loven.core.presentation.model.ResultUi
import com.sidspace.loven.modules.domain.repository.ModuleRepository
import com.sidspace.loven.modules.domain.usecase.GetModulesUseCase
import com.sidspace.loven.modules.presentation.mapper.toModuleUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(private val getModulesUseCase: GetModulesUseCase) : ViewModel() {

    private val _state = MutableStateFlow(ModuleState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ModuleEffect>()
    val effect = _effect.asSharedFlow()


    fun onIntent(intent: ModuleIntent) {
        when (intent) {
            is ModuleIntent.ToLessonsScreen -> toLessonsScreen(intent.idLanguage, intent.idModule)
        }
    }

    fun toLessonsScreen(idLanguage: String, idModule: String) {
        viewModelScope.launch {
            _effect.emit(ModuleEffect.ToLessonsScreen(idLanguage, idModule))
        }
    }

    fun getModules(idLanguage: String) {
        viewModelScope.launch {
            when (val data = getModulesUseCase(idLanguage)) {
                DomainResult.Error -> {
                    _state.update { it.copy(listModules = ResultUi.Error) }
                }

                is DomainResult.Success -> {
                    _state.update {
                        it.copy(
                            listModules =
                            ResultUi.Success(data.data.map { item -> item.toModuleUi() })
                        )
                    }
                }
            }
        }
    }
}
