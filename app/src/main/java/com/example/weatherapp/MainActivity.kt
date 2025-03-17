package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Coroutine to call the API
        lifecycleScope.launch {
            try {

                val lat = 30.0444
                val lon = 31.2357
                val apiKey = "557286fc08f4438364702631194d8280"

                val response = WeatherApi.retrofitService.getForecast(lat ,lon ,apiKey)
                setContent {
                    WeatherAppTheme {
                        HomeScreen(response)
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch weather data: ${e.message}")
            }
        }
    }

}
