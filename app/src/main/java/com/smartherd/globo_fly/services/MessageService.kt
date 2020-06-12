package com.smartherd.globo_fly.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface MessageService {

    @GET
    fun getMessages(@Url secondServer: String?): Call<String>
}