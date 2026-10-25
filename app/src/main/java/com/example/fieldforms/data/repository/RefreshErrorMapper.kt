package com.example.fieldforms.data.repository

import com.example.fieldforms.domain.repository.RefreshError
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun Exception.toRefreshError(): RefreshError =
    when (this) {
        is UnknownHostException, is ConnectException, is SocketTimeoutException -> RefreshError.Network
        is HttpException -> RefreshError.Http(code())
        is JsonEncodingException, is JsonDataException -> RefreshError.Unknown(message)
        is IOException -> RefreshError.Network
        else -> RefreshError.Unknown(message)
    }
