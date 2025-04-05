package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.local.SettingsRepositoryImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.home.HomeViewModelFactory
import com.example.weatherapp.settings.Language


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = WeatherDatabase.getInstance(applicationContext)
        val localDataSource = LocalDataSourceImpl(database.favoritesAndAlertsDao())
        val sharedPreferences = getSharedPreferences("weather_settings", MODE_PRIVATE)
        val repository = WeatherRepository.getInstance(localDataSource, sharedPreferences)
        val locationTracker = LocationTracker.getInstance(this)

        val settingsRepository = SettingsRepositoryImpl(sharedPreferences)

        val savedLanguage = settingsRepository.getLanguage()
        val languageCode = when (Language.valueOf(savedLanguage)) {
            Language.ARABIC -> "ar"
            Language.ENGLISH -> "en"
        }

        // Update the locale
        LocaleHelper.updateLocale(this, languageCode)


        val viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                repository,
                locationTracker,
                settingsRepository
            )
        )[HomeViewModel::class.java]



        setContent {
            WeatherAppTheme {
                MainScreen(viewModel, repository, settingsRepository)
            }
        }
    }


    }
