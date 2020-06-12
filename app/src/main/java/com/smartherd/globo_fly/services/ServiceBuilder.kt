package com.smartherd.globo_fly.services

import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.file.attribute.AclEntryType
import java.util.*
import java.util.concurrent.TimeUnit

object ServiceBuilder {

    // This will be our server address, chnage it before launching it to play-store
    private const val URL = "http://10.0.2.2:9000/"

    // Create Logger
    private val logger : HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // Creating a custom Interceptor to apply headers application wide
    val headerInterceptor: Interceptor = object : Interceptor{
        override fun intercept(chain: Interceptor.Chain): Response {

            var request = chain.request()

            request = request.newBuilder()
                        .addHeader("x-device-type", Build.DEVICE)
                        .addHeader("Accept-Language", Locale.getDefault().language)
                        .build()

            val response: Response = chain.proceed(request)

            return  response
        }

    }

    // Create OkHttp Client
    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()
                                                .callTimeout(15, TimeUnit.SECONDS)
                                                .addInterceptor(headerInterceptor)// Added for adding Headers Application wide
                                                .addInterceptor(logger)// Added for Intercept and logging

    // Create Retrofit builder
    private val builder : Retrofit.Builder = Retrofit.Builder().baseUrl(URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .client(okHttp.build())

    //Create Retrofit Instance
    private val retrofit : Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
}