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
                        Icon(Icons.Default.ArrowBack, "Back")
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
                    WeatherDetailContent(weather)
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
fun WeatherDetailContent(apiResponse: ApiResponse) {
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
            // Header Section
            Text(
                text = "${apiResponse.city?.name}, ${apiResponse.city?.country}",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))
            val currentWeather = apiResponse.list?.firstOrNull()

            Image(
                painter = getWeatherIcon(currentWeather?.weather?.firstOrNull()?.description ?: ""),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "${currentWeather?.main?.temp}°C",
                fontSize = 64.sp
            )
            Text(
                text = currentWeather?.weather?.firstOrNull()?.description?.capitalize() ?: "",
                fontSize = 18.sp
            )

            // Weather Details
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem("Humidity", "${currentWeather?.main?.humidity}%")
                WeatherDetailItem("Wind", "${currentWeather?.wind?.speed} km/h")
                WeatherDetailItem("Pressure", "${currentWeather?.main?.pressure} hPa")
            }

            // Hourly Forecast
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
                        temp = "${item?.main?.temp}°C"
                    )
                }
            }

            // 7-Day Forecast
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "7-Day Forecast",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
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