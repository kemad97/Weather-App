package com.example.weatherapp
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.model.local.SettingsRepository
import com.example.weatherapp.settings.Language
import com.example.weatherapp.settings.SettingsViewModel
import com.example.weatherapp.settings.TemperatureUnit
import com.example.weatherapp.settings.WindSpeedUnit
import com.example.weatherapp.utils.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var repository: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        repository = FakeSettingsRepository()
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_settings_load_correctly() = runTest {
        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val settings = (viewModel.settingsState.value as ResultState.Success).data
        assertThat(settings.temperatureUnit, `is`(TemperatureUnit.CELSIUS))
        assertThat(settings.windSpeedUnit, `is`(WindSpeedUnit.METER_PER_SEC))
        assertThat(settings.language, `is`(Language.ENGLISH))
    }



    @Test
    fun update_temperature_unit_updates_settings() = runTest {
        // When
        viewModel.updateTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val settings = (viewModel.settingsState.value as ResultState.Success).data
        assertThat(settings.temperatureUnit, `is`(TemperatureUnit.FAHRENHEIT))
        assertThat(repository.getTemperatureUnit(), `is`(TemperatureUnit.FAHRENHEIT.name))
    }




}