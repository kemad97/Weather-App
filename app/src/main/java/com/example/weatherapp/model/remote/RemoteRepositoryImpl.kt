package com.example.weatherapp.model.remote

import android.content.SharedPreferences
import com.example.weatherapp.model.local.SettingsRepository
import com.example.weatherapp.model.Response

class RemoteRepositoryImpl(
    private val apiService: WeatherApiService,
    private val sharedPreferences: SharedPreferences
) : RemoteRepository
{
    private val settingsRepository: SettingsRepository = SettingsRepository.getInstance( sharedPreferences)

    override suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response {
        val language = when (settingsRepository.getLanguage()) {
            "ARABIC" -> "ar"
            else -> "en"
        }
        return apiService.getForecast(lat, lon, apiKey, lang = language)
    }

}