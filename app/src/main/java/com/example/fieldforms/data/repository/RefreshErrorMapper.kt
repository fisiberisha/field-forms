package com.example.fieldforms.data.repository

import com.example.fieldforms.domain.repository.RefreshError
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

internal fun Exception.toRefreshError(): RefreshError =
    when (this) {
        is UnknownHostException, is IOException -> RefreshError.Network
        is HttpException -> RefreshError.Http(code())
        else -> RefreshError.Unknown(message)
    }
