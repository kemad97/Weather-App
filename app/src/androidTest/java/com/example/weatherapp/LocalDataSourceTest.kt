package com.example.weatherapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapp.model.local.Database.WeatherDatabase
import com.example.weatherapp.model.local.FavoriteEntity
import com.example.weatherapp.model.local.LocalDataSource
import com.example.weatherapp.model.local.LocalDataSourceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {
    private lateinit var database: WeatherDatabase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = LocalDataSourceImpl(database.favoritesAndAlertsDao())
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndGetFavorite() = runTest {
        val favorite = FavoriteEntity(
            cityName = "Cairo",
            lat = 31.2001,
            lon = 29.9187
        )

        localDataSource.insertFavorite(favorite)
        val favorites = localDataSource.getAllFavorites().first()

        assertThat(favorites.size, `is`(1))
        assertThat(favorites[0].cityName, `is`(favorite.cityName))
        assertThat(favorites[0].lat, `is`(favorite.lat))
        assertThat(favorites[0].lon, `is`(favorite.lon))
    }

    @Test
    fun insertAndDeleteFavorite() = runTest {
        val favorite = FavoriteEntity(
            cityName = "Cairo",
            lat = 25.6872,
            lon = 32.6396
        )
        localDataSource.insertFavorite(favorite)

        val fav = localDataSource.getAllFavorites().first()[0]
        localDataSource.deleteFavorite(fav)

        val loaded = localDataSource.getAllFavorites().first()
        assertThat(loaded.size, `is`(0))
    }
}