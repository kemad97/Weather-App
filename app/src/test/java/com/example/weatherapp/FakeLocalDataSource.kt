package com.example.weatherapp

import com.example.weatherapp.data.local.AlertEntity
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.data.local.LocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource : LocalDataSource {
    private val favorites = mutableListOf<FavoriteEntity>()
    private val alerts = mutableListOf<AlertEntity>()


    override fun getAllFavorites(): Flow<List<FavoriteEntity>> = flow {
        emit(favorites)
    }

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favorites.add(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favorites.remove(favorite)
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> = flow {
        emit(alerts)
    }

    override suspend fun insertAlert(alert: AlertEntity) {
        alerts.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertEntity) {
        alerts.remove(alert)
    }

}