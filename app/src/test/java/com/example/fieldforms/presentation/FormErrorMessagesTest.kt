package com.example.fieldforms.presentation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

class FormErrorMessagesTest {

    @Test
    fun noCachedDataMessage_networkErrors_areUserFriendly() {
        assertEquals(
            "Couldn't load the form. Check your connection and try again.",
            FormErrorMessages.noCachedDataMessage(IOException()),
        )
        assertEquals(
            "Couldn't load the form. Check your connection and try again.",
            FormErrorMessages.noCachedDataMessage(UnknownHostException()),
        )
    }

    @Test
    fun cachedDataRefreshMessage_networkErrors_mentionsSavedData() {
        assertEquals(
            "Couldn't refresh — you're offline. Showing saved data.",
            FormErrorMessages.cachedDataRefreshMessage(IOException()),
        )
    }
}
