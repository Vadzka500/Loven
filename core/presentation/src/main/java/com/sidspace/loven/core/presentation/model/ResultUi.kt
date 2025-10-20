package com.sidspace.loven.core.presentation.model

sealed class ResultUi<out T> {
    object Loading : ResultUi<Nothing>()
    data class Success<out T>(val data: T) : ResultUi<T>()
    object Error : ResultUi<Nothing>()
}
