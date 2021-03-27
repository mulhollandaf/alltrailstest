package com.test.alltrailstest

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("maps/api/place/nearbysearch/json?radius=1000&type=restaurant&key=$apiKey")
    suspend fun fetchRestaurants(
        @Query("keyword") search: String,
        @Query("location") location: String)
    : Response<RestaurantsDto>

    companion object {
        //TODO REMOVE
        const val apiKey = ""
    }
}
