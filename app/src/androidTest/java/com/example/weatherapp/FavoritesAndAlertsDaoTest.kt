package com.example.weatherapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.data.local.FavoritesAndAlertsDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavoritesAndAlertsDaoTest {

    private  lateinit var database: WeatherDatabase
    private lateinit var dao: FavoritesAndAlertsDao

    @Before
    fun setup()
    {
        database= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        dao=database.favoritesAndAlertsDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertFavoriteAndGet () = runTest {
        val favorite = FavoriteEntity(
            cityName = "Cairo",
            lat = 30.04,
            lon = 31.23
        )

        dao.insertFavorite(favorite)
        val loadedFav = dao.getAllFavorites().first()

        assertThat (loadedFav.size ,`is` (1))
        assertThat(loadedFav[0].cityName, `is` (favorite.cityName))
        assertThat(loadedFav[0].lat, `is` (favorite.lat))
        assertThat(loadedFav[0].lon, `is` (favorite.lon))

    }

    @Test
    fun deleteFavorite_retrieveEmpty() = runTest {
        val favorite = FavoriteEntity(
            cityName = "Giza",
            lat = 30.0444,
            lon = 31.2357
        )

        dao.insertFavorite(favorite)
        val insertedFavorite = dao.getAllFavorites().first()[0]

        dao.deleteFavorite(insertedFavorite)

        val loaded = dao.getAllFavorites().first()
        assertThat(loaded.size, `is`(0))
    }

}