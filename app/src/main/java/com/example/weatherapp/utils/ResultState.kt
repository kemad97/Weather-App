package com.example.weatherapp.utils


sealed class ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val exception: Throwable) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
    data object Empty : ResultState<Nothing>()
}