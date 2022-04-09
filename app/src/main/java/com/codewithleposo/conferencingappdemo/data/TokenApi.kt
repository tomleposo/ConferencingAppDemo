package com.codewithleposo.conferencingappdemo.data

import com.codewithleposo.conferencingappdemo.model.Token
import com.codewithleposo.conferencingappdemo.model.TokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    @POST("api/token")
    suspend fun requestAccessToken(@Body request: TokenRequest): Token
}