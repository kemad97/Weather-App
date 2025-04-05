package com.example.weatherapp

import android.content.SharedPreferences
import com.example.weatherapp.model.repo.WeatherRepository
import com.example.weatherapp.model.local.FavoriteEntity
import io.mockk.mockk
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
    private  lateinit var  sharedPreferences: SharedPreferences

    @Before
    fun createRepository() {
        localDataSource = FakeLocalDataSource()
        remoteRepository = FakeRemoteDataSource()
        sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        weatherRepository = WeatherRepository(localDataSource,   sharedPreferences,remoteRepository)
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

   


}