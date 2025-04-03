package com.example.weatherapp
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.ResultState
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.SettingsRepository
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.model.ApiResponse
import com.example.weatherapp.model.City
import com.example.weatherapp.viewmodel.FavoriteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var localDataSource: FakeLocalDataSource

    private lateinit var repository: WeatherRepository
    private lateinit var settingsRepository: SettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)


        repository = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)

        // Mock repository responses for local data
        coEvery { repository.getAllFavorites() } returns flow {
            emit(listOf(FavoriteEntity(
                id = 1,
                cityName = "Cairo",
                lat = 30.0,
                lon = 31.0
            )))
        }

        viewModel = FavoriteViewModel(repository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getFavorites_whenEmpty_returnsEmptyList() = runTest {
        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val favorites = viewModel.favorites.value
        assertThat(favorites.size, `is`(0))
    }

    @Test
    fun addFavorite_insertsToLocalDatabase() = runTest {
        // When
        viewModel.addFavorite("Cairo", 30.0, 31.0)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val favorites = viewModel.favorites.value
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].cityName, `is`("Cairo"))
        assertThat(favorites[0].lat, `is`(30.0))
        assertThat(favorites[0].lon, `is`(31.0))
    }



    @Test
    fun removeFavorite_deletesFromLocalDatabase() = runTest {
        // Given
        val favorite = FavoriteEntity(
            id = 1,
            cityName = "Cairo",
            lat = 30.0,
            lon = 31.0
        )

        // When
        viewModel.removeFavorite(favorite)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.deleteFavorite(favorite) }
    }
}