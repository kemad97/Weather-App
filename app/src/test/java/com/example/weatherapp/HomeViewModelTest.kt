import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.FakeRemoteDataSource
import com.example.weatherapp.LocationTracker
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.model.City
import com.example.weatherapp.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Rule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var locationTracker: LocationTracker
    private lateinit var settingsRepository: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Use FakeRemoteDataSource instead of mock
        repository = mockk(relaxed = true)
        locationTracker = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)

        // Mock location updates correctly
        coEvery { locationTracker.myLocation } returns MutableStateFlow(Pair(30.0, 31.0))

        // Mock repository response
        coEvery { repository.fetchWeather(any(), any(), any()) } returns flow {
            emit(ApiResponse(city = City(name = "Cairo")))
        }

        viewModel = HomeViewModel(repository, locationTracker, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchWeather_returnsWeatherData() = runTest {
        // When
        viewModel.observeLocationAndFetchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.weatherData.value
        assertThat(result, instanceOf(ResultState.Success::class.java))
        assertThat((result as ResultState.Success).data.city?.name, `is`("Cairo"))
    }
}