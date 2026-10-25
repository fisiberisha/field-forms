package com.example.fieldforms.presentation

import androidx.annotation.StringRes
import com.example.fieldforms.R
import com.example.fieldforms.domain.repository.RefreshError

object FormErrorMessages {

    @StringRes
    fun noCachedDataMessageRes(error: RefreshError): Int =
        when (error) {
            RefreshError.Network -> R.string.error_load_network
            is RefreshError.Http -> R.string.error_load_generic
            is RefreshError.Unknown -> R.string.error_load_generic
        }

    @StringRes
    fun cachedDataRefreshMessageRes(error: RefreshError): Int =
        when (error) {
            RefreshError.Network -> R.string.error_refresh_offline_cache
            is RefreshError.Http -> R.string.error_refresh_with_cache
            is RefreshError.Unknown -> R.string.error_refresh_with_cache
        }
}
