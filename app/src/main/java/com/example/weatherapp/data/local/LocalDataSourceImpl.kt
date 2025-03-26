package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl( private val favoriteDao: FavoriteDao) : LocalDataSource {
    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favoriteDao.deleteFavorite(favorite)
    }


}