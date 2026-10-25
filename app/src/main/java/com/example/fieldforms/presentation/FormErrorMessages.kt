package com.example.fieldforms.presentation

import java.io.IOException
import java.net.UnknownHostException

object FormErrorMessages {

    fun noCachedDataMessage(cause: Throwable): String =
        when (cause) {
            is UnknownHostException, is IOException ->
                "Couldn't load the form. Check your connection and try again."
            else ->
                cause.message ?: "Couldn't load the form. Please try again."
        }

    fun cachedDataRefreshMessage(cause: Throwable): String =
        when (cause) {
            is UnknownHostException, is IOException ->
                "Couldn't refresh — you're offline. Showing saved data."
            else ->
                "Couldn't refresh. Showing saved data."
        }
}
