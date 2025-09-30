package com.sidspace.core.domain.model

sealed class DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    object Error : DomainResult<Nothing>()
}
