package com.buisness.bonuscards.api

import com.buisness.bonuscards.api.repository.CardRepositoryImpl
import com.buisness.bonuscards.service.Constants.account_name
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Network {

    var BASE_URL = "https://$account_name.business.ru/api/rest/"

    lateinit var cardRepository: CardRepositoryImpl


    fun createRepository() {
        cardRepository = CardRepositoryImpl(getApi(getRetrofit()))
    }

    fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()



    private fun buildHttpClient(): OkHttpClient {
        val interceptor = buildInterceptor()
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private fun buildInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    inline fun <reified T> getApi(retrofit: Retrofit): T = retrofit.create(T::class.java)
}