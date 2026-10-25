package com.example.fieldforms.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object FormNetworkFactory {

    fun createApi(client: OkHttpClient = OkHttpClient()): FormApiService {
        val moshi = FormJsonParser.createMoshi()
        return Retrofit.Builder()
            .baseUrl(FormApiConstants.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FormApiService::class.java)
    }
}
