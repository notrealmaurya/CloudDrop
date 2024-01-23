package com.maurya.clouddrop.di

import com.maurya.clouddrop.api.LinksAPI
import com.maurya.clouddrop.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
    }


    @Singleton
    @Provides
    fun provideOKHTTPClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY  // Set the desired logging level
            })
            .retryOnConnectionFailure(true)
            .build()
    }




    @Singleton
    @Provides
    fun providesLinksAPI(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient)
            : LinksAPI {
        return retrofitBuilder
            .client(okHttpClient)
            .build().create(LinksAPI::class.java)
    }

}