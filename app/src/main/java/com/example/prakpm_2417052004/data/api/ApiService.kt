package com.example.prakpm_2417052004.data.api

import com.example.prakpm_2417052004.data.model.Food
import retrofit2.http.GET

interface ApiService {
    @GET("gistfile1.txt")
    suspend fun getFoods(): List<Food>
}
