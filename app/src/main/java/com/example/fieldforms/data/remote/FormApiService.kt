package com.example.fieldforms.data.remote

import com.example.fieldforms.data.remote.dto.PageDto
import retrofit2.http.GET

interface FormApiService {
    @GET(FormApiConstants.FORM_PATH)
    suspend fun getPages(): List<PageDto>
}
