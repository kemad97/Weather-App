@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.weatherapp.settings

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.utils.LocaleHelper
import com.example.weatherapp.R
import com.example.weatherapp.utils.ResultState
import com.example.weatherapp.home.EmptyScreen
import com.example.weatherapp.home.ErrorScreen
import com.example.weatherapp.home.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToMap: () -> Unit
) {
    val settingsState by viewModel.settingsState.collectAsState()
    val currentSettings by viewModel.currentSettings.collectAsState()
    val context = LocalContext.current

    var currentLanguage by remember { mutableStateOf(currentSettings?.language) }

    // Observe language changes and update the locale
//    LaunchedEffect(currentLanguage) {
//        currentLanguage?.let { language ->
//            val languageCode = when (language) {
//                Language.ARABIC -> "ar"
//                Language.ENGLISH -> "en"
//            }
//            LocaleHelper.updateLocale(context, languageCode)
//        }
//    }

    LaunchedEffect(currentLanguage) {
        currentLanguage?.let { language ->
            val languageCode = when (language) {
                Language.ARABIC -> "ar"
                Language.ENGLISH -> "en"
            }
            (context as? Activity)?.let {
                LocaleHelper.updateLocale(it, languageCode)
            }
        }
    }

    Scaffold (
        topBar = { TopAppBar(
            title = {Text(stringResource(R.string.settings))  }
        )
        }
    ){ padding ->
        when (settingsState) {
            is ResultState.Loading -> LoadingScreen()
            is ResultState.Success -> {
                val settings = (settingsState as ResultState.Success<Settings>).data
                SettingsContent(
                    settings = settings,
                    onLocationMethodChange = { method ->
                        viewModel.updateLocationMethod(method)
                        if (method == LocationMethod.MAP) {
                            onNavigateToMap()
                        }
                    },
                    onTemperatureUnitChange = viewModel::updateTemperatureUnit,
                    onWindSpeedUnitChange = viewModel::updateWindSpeedUnit,
                    onLanguageChange = { language ->
                        viewModel.updateLanguage(language)
                        currentLanguage = language
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            is ResultState.Error -> ErrorScreen((settingsState as ResultState.Error).exception.message)
            ResultState.Empty -> EmptyScreen()
        }
    }
}

@Preview (showSystemUi = true)
@Composable
private fun SettingsContent(
    settings: Settings,
    onLocationMethodChange: (LocationMethod) -> Unit,
    onTemperatureUnitChange: (TemperatureUnit) -> Unit,
    onWindSpeedUnitChange: (WindSpeedUnit) -> Unit,
    onLanguageChange: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location Method
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.location_method),
                    style = MaterialTheme.typography.titleMedium
                )
                LocationMethod.values().forEach { method ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.locationMethod==method,
                            onClick = {onLocationMethodChange(method) }
                        )
                        Text(
                            text = stringResource(
                                when (method) {
                                    LocationMethod.GPS -> R.string.gps
                                    LocationMethod.MAP -> R.string.map
                                }
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Temperature Unit
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.temperature_unit),
                    style = MaterialTheme.typography.titleMedium
                )
                TemperatureUnit.values().forEach { unit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.temperatureUnit == unit,
                            onClick = { onTemperatureUnitChange(unit) }
                        )
                        Text(
                            text = stringResource(
                                when (unit) {
                                    TemperatureUnit.CELSIUS-> R.string.celsius
                                    TemperatureUnit.FAHRENHEIT -> R.string.fahrenheit
                                    TemperatureUnit.KELVIN -> R.string.kelvin
                                }
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Wind Speed Unit
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.wind_speed_unit),
                    style = MaterialTheme.typography.titleMedium
                )
                WindSpeedUnit.values().forEach { unit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.windSpeedUnit == unit,
                            onClick = { onWindSpeedUnitChange(unit) }
                        )
                        Text(
                            text = stringResource(
                                when (unit) {
                                    WindSpeedUnit.METER_PER_SEC -> R.string.meter_per_sec
                                    WindSpeedUnit.MILES_PER_HOUR -> R.string.miles_per_hour
                                }
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Language
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.titleMedium
                )
                Language.values().forEach { language ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.language == language,
                            onClick = { onLanguageChange(language) }
                        )
                        Text(
                            text = stringResource(
                                when (language) {
                                    Language.ARABIC -> R.string.arabic
                                    Language.ENGLISH -> R.string.english
                                }
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

//private fun String.capitalize(): String {
//    return this.lowercase().replaceFirstChar { it.uppercase() }
//}