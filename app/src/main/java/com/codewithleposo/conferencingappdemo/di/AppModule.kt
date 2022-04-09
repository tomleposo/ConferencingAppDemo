package com.codewithleposo.conferencingappdemo.di

import android.app.Application
import com.codewithleposo.conferencingappdemo.BuildConfig.TOKEN_ENDPOINT_URL
import com.codewithleposo.conferencingappdemo.data.CallRepository
import com.codewithleposo.conferencingappdemo.data.TokenApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.hms.video.sdk.HMSSDK
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHMSSDK(context: Application) : HMSSDK{
        return HMSSDK.Builder(context)
            .build()
    }

    @Provides
    @Singleton
    fun provideAPI() : TokenApi {
        return Retrofit.Builder()
            .baseUrl(TOKEN_ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCallRepository(hmsSdk: HMSSDK, api: TokenApi) : CallRepository{
        return CallRepository(
            tokenApi = api,
            HmsSdk = hmsSdk
        )
    }
}