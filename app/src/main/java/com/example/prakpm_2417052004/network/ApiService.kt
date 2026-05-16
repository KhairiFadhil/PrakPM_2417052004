package com.example.prakpm_2417052004.network

import model.Food
import retrofit2.http.GET

interface ApiService {
    @GET("raw")
    suspend fun getFoods(): List<Food>
}
