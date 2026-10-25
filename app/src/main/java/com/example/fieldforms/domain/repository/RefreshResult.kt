package com.example.fieldforms.domain.repository

sealed interface RefreshResult {
    data object Success : RefreshResult

    data class Failure(
        val cause: Throwable,
    ) : RefreshResult
}
