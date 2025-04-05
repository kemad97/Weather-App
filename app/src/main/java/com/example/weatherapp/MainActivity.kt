package com.example.weatherapp

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.local.SettingsRepositoryImpl
import com.example.weatherapp.model.local.Database.WeatherDatabase
import com.example.weatherapp.model.repo.WeatherRepository
import com.example.weatherapp.model.local.LocalDataSourceImpl
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.home.HomeViewModelFactory
import com.example.weatherapp.settings.Language
import com.example.weatherapp.utils.LocationTracker
import com.example.weatherapp.utils.MainScreen
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { updateLocale2(it) })
    }

     fun updateLocale2(context: Context): Context {
        val sharedPreferences = context.getSharedPreferences("weather_settings", MODE_PRIVATE)
        val settingsRepository = SettingsRepositoryImpl(sharedPreferences)
        val savedLanguage = settingsRepository.getLanguage()
        val locale = when (Language.valueOf(savedLanguage)) {
            Language.ARABIC -> Locale("ar")
            Language.ENGLISH -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = WeatherDatabase.getInstance(applicationContext)
        val localDataSource = LocalDataSourceImpl(database.favoritesAndAlertsDao())
        val sharedPreferences = getSharedPreferences("weather_settings", MODE_PRIVATE)
        val repository = WeatherRepository.getInstance(localDataSource, sharedPreferences)
        val locationTracker = LocationTracker.getInstance(this)

        val settingsRepository = SettingsRepositoryImpl(sharedPreferences)

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

