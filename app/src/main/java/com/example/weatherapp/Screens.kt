@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.weatherapp


import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Preview(showSystemUi = true)
@Composable
fun HomeScreen(viewModel: WeatherViewModel) {
    val weatherState by viewModel.weatherData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeather()
    }

    if (weatherState == null) {
        CircularProgressIndicator()
    } else {
        WeatherScreen(weatherState!!)
    }
}


@Composable
fun WeatherScreen(response:  Response) {

    val cityName = response.city?.name ?: "Unknown"
    val country = response.city?.country ?: ""
    val temperature = response.list?.firstOrNull()?.main?.temp?.toString() ?: "--"
    val description = response.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "Unknown"
    val humidity = response.list?.firstOrNull()?.main?.humidity?.toString() ?: "--"
    val windSpeed = response.list?.firstOrNull()?.wind?.speed?.toString() ?: "--"
    val pressure = response.list?.firstOrNull()?.main?.pressure?.toString() ?: "--"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // City Name
        Text(
            text = "$cityName, $country",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Weather Icon (Use dynamic icon logic)
        val weatherIcon: Painter = painterResource(id = R.drawable.weather_ic) // Replace with logic to select icon dynamically
        Image(
            painter = weatherIcon,
            contentDescription = "Weather Icon",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Temperature
        Text(
            text = "$temperature°C",
            fontSize = 68.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Weather Description
        Text(
            text = description,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weather Details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem("Humidity", "$humidity%")
            WeatherDetailItem("Wind", "$windSpeed km/h")
            WeatherDetailItem("Pressure", "$pressure hPa")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hourly Forecast
        Text(
            text = "Hourly Forecast",
            fontSize = 20.sp
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(response.list ?: emptyList()) { item ->
                HourlyWeatherItem(item?.dtTxt ?: "--", "${item?.main?.temp ?: "--"}°C")
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
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = time, fontSize = 14.sp)
        Image(
            painter = painterResource(id = R.drawable.weather_ic), // Replace with real icon
            contentDescription = "Weather Icon",
            modifier = Modifier.size(40.dp)
        )
        Text(text = temp, fontSize = 16.sp)
    }
}


@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
