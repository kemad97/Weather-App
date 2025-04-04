@file:Suppress("PreviewAnnotationInFunctionWithParameters")

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ResultState
import com.example.weatherapp.home.EmptyScreen
import com.example.weatherapp.home.ErrorScreen
import com.example.weatherapp.home.LoadingScreen
import com.example.weatherapp.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToMap: () -> Unit
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Scaffold (
        topBar = { TopAppBar(
            title = {Text("Settings")  }
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
                    onLanguageChange = viewModel::updateLanguage,
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
                    text = "Location Method",
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
                            text = method.name,
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
                    text = "Temperature Unit",
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
                            text = unit.name,
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
                    text = "Wind Speed Unit",
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
                            text = unit.name.replace("_", " "),
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
                    text = "Language",
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
                            text = language.name.capitalize(),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}