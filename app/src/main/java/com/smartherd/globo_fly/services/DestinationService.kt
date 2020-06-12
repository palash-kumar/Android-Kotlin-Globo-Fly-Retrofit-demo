package com.smartherd.globo_fly.services

import com.smartherd.globo_fly.models.Destination
import retrofit2.Call
import retrofit2.http.*

interface DestinationService {

    /* Manually added headers
    @Headers("x-device-type: Android", "x-foo: bar")
    @GET("destination")
    fun getDestinationList(@Header("Accept-Language") language: String?, @Header("x-App-Name") appNam: String): Call<List<Destination>>*/

    @GET("destination")
    fun getDestinationList(): Call<List<Destination>>

    @GET("destination/{id}")
    fun getDestination(@Path("id")id: Int):Call<Destination>

    @GET("destination")
    fun getDestinationList(@Query("country") country : String): Call<List<Destination>>

    @POST("destination")
    fun addDestination(@Body newDestination: Destination): Call<Destination>

    @FormUrlEncoded
    @PUT("destination/{id}")
    fun updateDestination(@Path("id") id: Int,
                          @Field("city") city: String,
                          @Field("description") desc: String,
                          @Field("country") country: String
    ): Call<Destination>

    @DELETE("destination/{id}")
    fun deleteDestination(@Path("id") id : Int): Call<Unit>
}