package com.example.weatherapp.data

import com.example.weatherapp.model.ApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // in Celsius
    ): ApiResponse


    @GET("forecast") //fetches the 5-day forecast
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // in Celsius
    ): ApiResponse


}
const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}




