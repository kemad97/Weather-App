package com.example.weatherapp.favorites

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.ResultState
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.home.DailyForecastItem
import com.example.weatherapp.home.HourlyWeatherItem
import com.example.weatherapp.home.WeatherDetailItem
import com.example.weatherapp.home.formatTime
import com.example.weatherapp.home.getWeatherIcon
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailScreen(
    lat: Double,
    lon: Double,
    viewModel: FavoriteViewModel,
    onNavigateBack: () -> Unit
) {
    val weatherState by viewModel.selectedWeather.collectAsState()

    LaunchedEffect(lat, lon) {
        viewModel.getWeatherForLocation(lat, lon)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = weatherState) {
                is ResultState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ResultState.Success -> {
                    val weather = state.data
                    WeatherDetailContent(weather, viewModel)
                }

                is ResultState.Error -> {
                    Text(
                        text = "Error: ${state.exception.message}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun WeatherDetailContent(
    apiResponse: ApiResponse, viewModel: FavoriteViewModel
) {
    val settings by viewModel.settings.collectAsState()

    val cityName = apiResponse.city?.name ?: "--"
    val country = apiResponse.city?.country ?: "--"
    val temperature = apiResponse.list?.firstOrNull()?.main?.temp?.toString() ?: "--"
    val description =
        apiResponse.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "--"
    val humidity = apiResponse.list?.firstOrNull()?.main?.humidity?.toString() ?: "--"
    val windSpeed = apiResponse.list?.firstOrNull()?.wind?.speed?.toString() ?: "--"
    val pressure = apiResponse.list?.firstOrNull()?.main?.pressure?.toString() ?: "--"

    val tempMax = apiResponse.list?.firstOrNull()?.main?.tempMax?.toString() ?: "--"
    val tempMin = apiResponse.list?.firstOrNull()?.main?.tempMin?.toString() ?: "--"


    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).apply {
            this.timeZone = timeZone
        }.format(Date())
    }


    val tempUnit = when (settings?.temperatureUnit) {
        TemperatureUnit.CELSIUS -> stringResource(R.string.c)
        TemperatureUnit.FAHRENHEIT -> stringResource(R.string.f)
        TemperatureUnit.KELVIN -> stringResource(R.string.k)
        null -> stringResource(R.string.c)
    }

    val windUnit = when (settings?.windSpeedUnit) {
        WindSpeedUnit.METER_PER_SEC -> stringResource(R.string.m_s)
        WindSpeedUnit.MILES_PER_HOUR -> stringResource(R.string.mph)
        null -> stringResource(R.string.m_s)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AsyncImage(
            model = R.drawable.beautifulmountains,
            contentDescription = "Weather Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Main Weather Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Date and Location
                        Text(
                            text = currentDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$cityName, $country",
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weather Icon and Temperature
                        Image(
                            painter = getWeatherIcon(description),
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$temperature $tempUnit",
                            fontSize = 64.sp
                        )
                        Text(
                            text = description,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // High/Low Temperature
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weather Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WeatherDetailItem(stringResource(R.string.humidity), "$humidity%")
                            WeatherDetailItem(stringResource(R.string.wind), "$windSpeed $windUnit")
                            WeatherDetailItem(stringResource(R.string.pressure), "$pressure hPa")
                        }
                    }
                }

                // Hourly Forecast (outside the card)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Hourly Forecast",
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
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

                // Daily Forecast Section
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string._7_day_forecast),
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Daily Forecast Items
                Column {
                    apiResponse.list
                        ?.filterNotNull()
                        ?.groupBy { it.dtTxt?.substring(0, 10) }
                        ?.map { it.value.first() }
                        ?.take(8)
                        ?.forEach { forecast ->
                            DailyForecastItem(forecast, tempUnit)
                        }
                }
            }
        }
    }
//    Box(modifier = Modifier.fillMaxSize()) {
//        AsyncImage(
//            model = R.drawable.beautifulmountains,
//            contentDescription = "Weather Background",
//            contentScale = ContentScale.FillBounds,
//            modifier = Modifier.fillMaxSize()
//        )
//
//        LazyColumn (
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            item {
//                Text(text = "$cityName, $country", fontSize = 28.sp, textAlign = TextAlign.Center)
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Image(
//                    painter = getWeatherIcon(description),
//                    contentDescription = "Weather Icon",
//                    modifier = Modifier.size(100.dp)
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = "$temperature $tempUnit", fontSize = 64.sp)
//                Text(text = description, fontSize = 22.sp)
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//            // Weather details
//            item {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    WeatherDetailItem("Humidity", "$humidity%")
//                    WeatherDetailItem("Wind", "$windSpeed $windUnit")
//                    WeatherDetailItem("Pressure", "$pressure hPa")
//                }
//            }
//
//            // hourly forecase
//            item {
//
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(text = "Hourly Forecast", fontSize = 20.sp)
//                LazyRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                ) {
//                    items(apiResponse.list?.take(8) ?: emptyList()) { item ->
//                        HourlyWeatherItem(
//                            time = com.example.weatherapp.ui.screens.formatTime(item?.dtTxt ?: ""),
//                            temp = "${item?.main?.temp ?: "--"}$tempUnit"
//                        )
//                    }
//                }
//            }
//            item {
//
//                // 5 day forecase
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(
//                    text = "7-Day Forecast",
//                    fontSize = 20.sp,
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                )
//            }
//
////            LazyColumn(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .fillMaxSize()
////                    .padding(vertical = 8.dp),
////                verticalArrangement = Arrangement.spacedBy(4.dp)
////            ) {
//            val dailyForecasts = apiResponse.list
//                ?.filterNotNull()
//                ?.groupBy { it.dtTxt?.substring(0, 10) }
//                ?.map { it.value.first() }
//                ?.take(5)
//                ?: emptyList()
//
//            items(dailyForecasts) { forecast ->
//                DailyForecastItem(forecast, tempUnit)
//            }
//
//            //  }
//        }
//    }


}




