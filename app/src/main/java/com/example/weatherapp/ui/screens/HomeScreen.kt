@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.weatherapp.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.weatherapp.LocationTracker
import com.example.weatherapp.R
import com.example.weatherapp.ResultState
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.model.ListItem
import com.example.weatherapp.viewmodel.WeatherViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun HomeScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherState by viewModel.weatherData.collectAsState()

    Scaffold(

    ) {paddingValues->
        Box(modifier = Modifier.padding(paddingValues)) {
        when (weatherState) {
            is ResultState.Loading -> LoadingScreen()
            is ResultState.Success -> WeatherScreen((weatherState as ResultState.Success<ApiResponse>).data)
            is ResultState.Error -> ErrorScreen((weatherState as ResultState.Error).exception.message)
            ResultState.Empty -> EmptyScreen()
        }
        }
    }
}

@Composable
fun WeatherScreen(apiResponse: ApiResponse) {
    val cityName = apiResponse.city?.name ?: "Unknown"
    val country = apiResponse.city?.country ?: ""
    val temperature = apiResponse.list?.firstOrNull()?.main?.temp?.toString() ?: "--"
    val description = apiResponse.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "Unknown"
    val humidity = apiResponse.list?.firstOrNull()?.main?.humidity?.toString() ?: "--"
    val windSpeed = apiResponse.list?.firstOrNull()?.wind?.speed?.toString() ?: "--"
    val pressure = apiResponse.list?.firstOrNull()?.main?.pressure?.toString() ?: "--"

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.sunnyg)
                .crossfade(true)
                .decoderFactory(GifDecoder.Factory())
                .build(),
            contentDescription = "Animated Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$cityName, $country", fontSize = 24.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = getWeatherIcon(description),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$temperature°C", fontSize = 64.sp)
            Text(text = description, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem("Humidity", "$humidity%")
                WeatherDetailItem("Wind", "$windSpeed km/h")
                WeatherDetailItem("Pressure", "$pressure hPa")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Hourly Forecast", fontSize = 20.sp)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(apiResponse.list?.take(8) ?: emptyList()) { item ->
                    HourlyWeatherItem(
                        time = formatTime(item?.dtTxt ?: ""),
                        temp = "${item?.main?.temp ?: "--"}°C"
                    )
                }
            }

            // 7 day forecase
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "7-Day Forecast", fontSize = 20.sp)
            LazyRow  (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            ) {
                val dailyForecasts = apiResponse.list
                    ?.filterNotNull()
                    ?.groupBy { it.dtTxt?.substring(0, 10) }
                    ?.map { it.value.first() }
                    ?.take(7)
                    ?: emptyList()

                items(dailyForecasts) { forecast ->
                    DailyForecastItem(forecast)
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: ListItem) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDate(forecast.dtTxt ?: ""),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = getWeatherIcon(forecast.weather?.firstOrNull()?.description ?: ""),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${forecast.main?.temp ?: "--"}°C", fontSize = 16.sp)
            Text(
                text = forecast.weather?.firstOrNull()?.description ?: "",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WeatherDetailItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 14.sp)
        Text(text = value, fontSize = 16.sp)
    }
}

@Composable
fun HourlyWeatherItem(time: String, temp: String) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = time, fontSize = 14.sp)
            Image(
                painter = painterResource(id = R.drawable.weather_ic),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(40.dp)
            )
            Text(text = temp, fontSize = 16.sp)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun getWeatherIcon(description: String): Painter {
    return when {
        description.contains("cloud", ignoreCase = true) -> painterResource(id = R.drawable.weather_ic)
        description.contains("rain", ignoreCase = true) -> painterResource(id = R.drawable.weather_ic)
        description.contains("sun", ignoreCase = true) -> painterResource(id = R.drawable.weather_ic)
        else -> painterResource(id = R.drawable.weather_ic)
    }
}

private fun formatDate(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        outputFormat.format(date ?: return dateStr)
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatTime(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        outputFormat.format(date ?: return dateStr)
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun EmptyScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Spacer(modifier = Modifier.height(16.dp).padding(16.dp))
        Button(onClick = {
            LocationTracker.getInstance(context).checkLocationSettings()

        }) {
            Text("Refresh")
        }
    }
}

@Composable
fun ErrorScreen(message: String?) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "An error occurred",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = {
            LocationTracker.getInstance(context).checkLocationSettings()

        }) {
            Text("Refresh")
        }
    }
}