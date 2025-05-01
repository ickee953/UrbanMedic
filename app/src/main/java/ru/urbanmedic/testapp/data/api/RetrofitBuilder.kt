/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    const val USER_BASE_URL = "https://randomuser.me"
    const val USER_API_PATH = "api"
    const val USER_API_RESULTS = 20
    const val GEO_BASE_URL = "https://suggestions.dadata.ru"
    const val GEO_PATH = "/suggestions/api/4_1/rs/geolocate/address"
    const val GEO_AUTH_TOKEN = "0fc7d60da65943f6aa3ba2f4a289b50bc024d18f"

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("$USER_BASE_URL/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun buildGeoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("$GEO_BASE_URL/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService = buildRetrofit().create(ApiService::class.java)
    val geoService: GeoService = buildGeoRetrofit().create(GeoService::class.java)
}