package com.example.prakpm_2417052004.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // TODO: ganti dengan raw URL Gist kamu sendiri.
    // Format: https://gist.githubusercontent.com/<username>/<gist_id>/
    private const val BASE_URL =
        "https://gist.githubusercontent.com/USERNAME/GIST_ID/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
