@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.weatherapp.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.weatherapp.LocationTracker
import com.example.weatherapp.R
import com.example.weatherapp.ResultState
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.model.ListItem
import com.example.weatherapp.viewmodel.HomeViewModel
import com.example.weatherapp.viewmodel.LocationMethod
import com.example.weatherapp.viewmodel.Settings
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

    val weatherState by viewModel.weatherData.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast") },
                actions = {
                    IconButton(onClick = { viewModel.observeLocationAndFetchWeather() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (weatherState) {
                is ResultState.Loading -> LoadingScreen()
                is ResultState.Success -> {
                    WeatherScreen(
                        (weatherState as ResultState.Success<ApiResponse>).data,
                        settings = settings ?: Settings()
                    )

                }
                is ResultState.Error -> ErrorScreen((weatherState as ResultState.Error).exception.message)
                ResultState.Empty -> EmptyScreen()
            }
        }

    }
}
@Preview (showSystemUi = true)
@Composable
fun WeatherScreen(apiResponse: ApiResponse, settings: Settings?) {
    val cityName = apiResponse.city?.name ?: "Unknown"
    val country = apiResponse.city?.country ?: ""
    val temperature = apiResponse.list?.firstOrNull()?.main?.temp?.toString() ?: "--"
    val description =
        apiResponse.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "Unknown"
    val humidity = apiResponse.list?.firstOrNull()?.main?.humidity?.toString() ?: "--"
    val windSpeed = apiResponse.list?.firstOrNull()?.wind?.speed?.toString() ?: "--"
    val pressure = apiResponse.list?.firstOrNull()?.main?.pressure?.toString() ?: "--"

    val tempMax = apiResponse.list?.firstOrNull()?.main?.tempMax?.toString() ?: "--"
    val tempMin = apiResponse.list?.firstOrNull()?.main?.tempMin?.toString() ?: "--"

    val tempUnit = when (settings?.temperatureUnit) {
        TemperatureUnit.CELSIUS -> stringResource(R.string.c)
        TemperatureUnit.FAHRENHEIT -> stringResource(R.string.f)
        TemperatureUnit.KELVIN -> stringResource(R.string.k)
        null -> stringResource(R.string.c)
    }

    val windUnit = when (settings?.windSpeedUnit) {
        WindSpeedUnit.METER_PER_SEC -> "m/s"
        WindSpeedUnit.MILES_PER_HOUR -> "mph"
        null -> "m/s"

    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AsyncImage(
            //    model = getWeatherBackground(description),
            model = R.drawable.beautifulmountains,
            contentDescription = "Weather Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
        )

        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "$cityName, $country", fontSize = 28.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = getWeatherIcon(description),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "$temperature $tempUnit", fontSize = 64.sp)
                Text(text = description, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(16.dp))

                //h l temp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "H: $tempMax$tempUnit",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "L: $tempMin$tempUnit",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            // Weather details
            item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(stringResource(R.string.humidity), "$humidity%")
                WeatherDetailItem(stringResource(R.string.wind), "$windSpeed $windUnit")
                WeatherDetailItem(stringResource(R.string.pressure), "$pressure hPa")
            }
                }

            // hourly forecase
            item {

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
                            temp = "${item?.main?.temp ?: "--"}$tempUnit"
                        )
                    }
                }
            }
            item {

                // 5 day forecase
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string._7_day_forecast),
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Daily forecast
                val dailyForecasts = apiResponse.list
                    ?.filterNotNull()
                    ?.groupBy { it.dtTxt?.substring(0, 10) }
                    ?.map { it.value.first() }
                    ?.take(8)
                    ?: emptyList()

                items(dailyForecasts) { forecast ->
                    DailyForecastItem(forecast, tempUnit)
                }

          //  }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: ListItem, tempUnit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date
            Text(
                text = formatDate(forecast.dtTxt ?: ""),
                style = MaterialTheme.typography.titleMedium
            )

            // Weather icon and description
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = getWeatherIcon(forecast.weather?.firstOrNull()?.description ?: ""),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = forecast.weather?.firstOrNull()?.description?.capitalize() ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Temperature
            Text(
                text = "${forecast.main?.temp?.toString()}$tempUnit",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun WeatherDetailItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            painter = when (title) {
                "Humidity" -> painterResource(id = R.drawable.ic_humidty)
                "Wind" -> painterResource(id = R.drawable.ic_wind)
                "Pressure" -> painterResource(id = R.drawable.ic_pressre)
                else -> painterResource(id = R.drawable.weather_ic)
            },
            contentDescription = title,
            modifier = Modifier.size(26.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

//@Composable
//fun getWeatherBackground(description: String): Int {
//    return when {
//        description.contains("cloud", ignoreCase = true) -> R.drawable.cloudy
//        description.contains("rain", ignoreCase = true) -> R.drawable.bk_rain
//        description.contains("snow", ignoreCase = true) -> R.drawable.bk_snow
//        description.contains("sun", ignoreCase = true) -> R.drawable.beautifulmountains
//        description.contains("clear", ignoreCase = true) -> R.drawable.sunnyg
//        else -> R.drawable.sunnyg
//    }
//}


@Composable
fun getWeatherIconByTemp(temp: String): Painter {
    val temperature = temp.replace("°C", "").toFloatOrNull() ?: 0f
    return when {
        temperature >= 35 -> painterResource(id = R.drawable.ic_sunny)
        temperature >= 20 -> painterResource(id = R.drawable.weather_ic)
        temperature >= 10 -> painterResource(id = R.drawable.ic_haze)
        temperature >= 0 -> painterResource(id = R.drawable.ic_snow)
        else -> painterResource(id = R.drawable.weather_ic)
    }
}

@Composable
fun HourlyWeatherItem(time: String, temp: String, description: String = "") {
    Card(
        modifier = Modifier.padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = time, fontSize = 14.sp)
            Image(
                painter = getWeatherIconByTemp(temp),
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
        description.contains(
            "cloud",
            ignoreCase = true
        ) -> painterResource(id = R.drawable.weather_ic)

        description.contains(stringResource(R.string.rain), ignoreCase = true) -> painterResource(id = R.drawable.ic_rain)
        description.contains(stringResource(R.string.sun), ignoreCase = true) -> painterResource(id = R.drawable.ic_sunny)
        description.contains(
            "clear",
            ignoreCase = true
        ) -> painterResource(id = R.drawable.ic_sunny)

        description.contains(stringResource(R.string.snow), ignoreCase = true) -> painterResource(id = R.drawable.ic_snow)

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

fun formatTime(dateStr: String): String {
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

        Spacer(
            modifier = Modifier
                .height(16.dp)
                .padding(16.dp)
        )
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
            text = message ?: stringResource(R.string.an_error_occurred),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
//        Button(onClick = {
//            LocationTracker.getInstance(context).getLocationUpdates()
//
//        }) {
//            Text("Refresh")
//        }
    }
}