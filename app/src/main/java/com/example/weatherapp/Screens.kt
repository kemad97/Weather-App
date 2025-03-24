@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.weatherapp

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
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.viewmodel.WeatherViewModel

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


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            Spacer(modifier = Modifier.height(8.dp))
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

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(apiResponse.list ?: emptyList()) { item ->
                    HourlyWeatherItem(item?.dtTxt ?: "--", "${item?.main?.temp ?: "--"}°C")
                }
            }
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
        else -> painterResource(id = R.drawable.weather_ic) // Default icon
    }
}

@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No weather data available",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorScreen(message: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "An error occurred",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}