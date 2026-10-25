package com.example.fieldforms.domain.repository

sealed interface RefreshError {
    data object Network : RefreshError

    data class Http(
        val code: Int,
    ) : RefreshError

    data class Unknown(
        val message: String? = null,
    ) : RefreshError
}
