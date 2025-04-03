package com.example.weatherapp

import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.AlertEntity
import com.example.weatherapp.data.local.FavoriteEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {
    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var remoteRepository: FakeRemoteDataSource
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun createRepository() {
        localDataSource = FakeLocalDataSource()
        remoteRepository = FakeRemoteDataSource()
        weatherRepository = WeatherRepository(localDataSource, remoteRepository)
    }

    @Test
    fun insertAndGetFavorite() = runTest {
        // Given
        val favorite = FavoriteEntity(
            cityName = "Cairo",
            lat = 30.0444,
            lon = 31.2357
        )

        // When
        weatherRepository.insertFavorite(favorite)
        val favorites = weatherRepository.getAllFavorites().first()

        // Then
        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].cityName, `is`(favorite.cityName))
        assertThat(favorites[0].lat, `is`(favorite.lat))
        assertThat(favorites[0].lon, `is`(favorite.lon))
    }

    @Test
    fun insertAndDeleteFavorite() = runTest {
        // Given
        val favorite = FavoriteEntity(
            cityName = "Alexandria",
            lat = 31.2001,
            lon = 29.9187
        )

        // When
        weatherRepository.insertFavorite(favorite)
        val initialFavorites = weatherRepository.getAllFavorites().first()
        weatherRepository.deleteFavorite(initialFavorites[0])

        // Then
        val finalFavorites = weatherRepository.getAllFavorites().first()
        assertThat(finalFavorites.size, `is`(0))
    }

    @Test
    fun fetchWeatherData_success() = runTest {
        // When
        val response = weatherRepository.fetchWeather(0.0, 0.0, "test_key").first()

        // Then
        assertThat(response.city?.name, `is`("Cairo"))
    }


}