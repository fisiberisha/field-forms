package com.example.fieldforms.data.repository

import com.example.fieldforms.domain.repository.RefreshError
import com.squareup.moshi.JsonEncodingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import okhttp3.ResponseBody.Companion.toResponseBody

class RefreshErrorMapperTest {

    @Test
    fun mapsConnectivityExceptionsToNetworkError() {
        assertEquals(RefreshError.Network, UnknownHostException().toRefreshError())
        assertEquals(RefreshError.Network, ConnectException().toRefreshError())
        assertEquals(RefreshError.Network, SocketTimeoutException().toRefreshError())
    }

    @Test
    fun mapsMoshiFailuresToUnknownNotNetwork() {
        val jsonError = JsonEncodingException("Expected BEGIN_OBJECT but was STRING at path \$")
        assertEquals(RefreshError.Unknown(jsonError.message), jsonError.toRefreshError())
    }

    @Test
    fun mapsGenericIOExceptionToNetworkError() {
        assertEquals(RefreshError.Network, IOException("broken pipe").toRefreshError())
    }

    @Test
    fun mapsHttpExceptionToHttpError() {
        val response = Response.error<String>(
            503,
            "error".toResponseBody(),
        )
        val error = HttpException(response).toRefreshError()

        assertTrue(error is RefreshError.Http)
        assertEquals(503, (error as RefreshError.Http).code)
    }

    @Test
    fun mapsOtherExceptionsToUnknown() {
        val error = IllegalStateException("boom").toRefreshError()

        assertTrue(error is RefreshError.Unknown)
        assertEquals("boom", (error as RefreshError.Unknown).message)
    }
}
