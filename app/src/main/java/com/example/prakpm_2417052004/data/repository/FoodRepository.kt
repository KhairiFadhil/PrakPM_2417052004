package com.example.prakpm_2417052004.data.repository

import com.example.prakpm_2417052004.data.api.RetrofitClient
import com.example.prakpm_2417052004.data.model.Food

class FoodRepository {
    suspend fun getFoods(): List<Food> {
        return try {
            RetrofitClient.instance.getFoods()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
