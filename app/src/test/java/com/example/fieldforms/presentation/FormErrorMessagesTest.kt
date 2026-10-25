package com.example.fieldforms.presentation

import com.example.fieldforms.R
import com.example.fieldforms.domain.repository.RefreshError
import org.junit.Assert.assertEquals
import org.junit.Test

class FormErrorMessagesTest {

    @Test
    fun noCachedDataMessageRes_mapsNetworkToLoadNetworkString() {
        assertEquals(
            R.string.error_load_network,
            FormErrorMessages.noCachedDataMessageRes(RefreshError.Network),
        )
        assertEquals(
            R.string.error_load_generic,
            FormErrorMessages.noCachedDataMessageRes(RefreshError.Http(500)),
        )
    }

    @Test
    fun cachedDataRefreshMessageRes_mapsNetworkToOfflineCacheString() {
        assertEquals(
            R.string.error_refresh_offline_cache,
            FormErrorMessages.cachedDataRefreshMessageRes(RefreshError.Network),
        )
        assertEquals(
            R.string.error_refresh_with_cache,
            FormErrorMessages.cachedDataRefreshMessageRes(RefreshError.Unknown("x")),
        )
    }
}
