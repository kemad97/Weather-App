package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(favorite: FavoriteEntity)
}