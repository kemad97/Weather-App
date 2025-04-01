import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.weatherapp.R
import com.example.weatherapp.ResultState
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.ui.screens.DailyForecastItem
import com.example.weatherapp.ui.screens.HourlyWeatherItem
import com.example.weatherapp.ui.screens.WeatherDetailItem
import com.example.weatherapp.ui.screens.getWeatherIcon
import com.example.weatherapp.viewmodel.FavoriteViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel
import com.example.weatherapp.viewmodel.TemperatureUnit
import com.example.weatherapp.viewmodel.WindSpeedUnit
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

    val cityName = apiResponse.city?.name ?: "Unknown"
    val country = apiResponse.city?.country ?: ""
    val temperature = apiResponse.list?.firstOrNull()?.main?.temp?.toString() ?: "--"
    val description =
        apiResponse.list?.firstOrNull()?.weather?.firstOrNull()?.description ?: "Unknown"
    val humidity = apiResponse.list?.firstOrNull()?.main?.humidity?.toString() ?: "--"
    val windSpeed = apiResponse.list?.firstOrNull()?.wind?.speed?.toString() ?: "--"
    val pressure = apiResponse.list?.firstOrNull()?.main?.pressure?.toString() ?: "--"

    val tempUnit = when (settings?.temperatureUnit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.FAHRENHEIT -> "°F"
        TemperatureUnit.KELVIN -> "K"
        null -> "°C"
    }

    val windUnit = when (settings?.windSpeedUnit) {
        WindSpeedUnit.METER_PER_SEC -> "m/s"
        WindSpeedUnit.MILES_PER_HOUR -> "mph"
        null -> "m/s"

    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.beautifulmountains,
            contentDescription = "Weather Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
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
            }
            // Weather details
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItem("Humidity", "$humidity%")
                    WeatherDetailItem("Wind", "$windSpeed $windUnit")
                    WeatherDetailItem("Pressure", "$pressure hPa")
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
                            time = com.example.weatherapp.ui.screens.formatTime(item?.dtTxt ?: ""),
                            temp = "${item?.main?.temp ?: "--"}$tempUnit"
                        )
                    }
                }
            }
            item {

                // 5 day forecase
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "7-Day Forecast",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxSize()
//                    .padding(vertical = 8.dp),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
            val dailyForecasts = apiResponse.list
                ?.filterNotNull()
                ?.groupBy { it.dtTxt?.substring(0, 10) }
                ?.map { it.value.first() }
                ?.take(5)
                ?: emptyList()

            items(dailyForecasts) { forecast ->
                DailyForecastItem(forecast, tempUnit)
            }

            //  }
        }
    }


}




