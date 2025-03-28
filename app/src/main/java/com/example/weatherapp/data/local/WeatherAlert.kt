package com.example.weatherapp.data.local

data class WeatherAlert(
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val type: AlertType,
    val isEnabled: Boolean = true
)

enum class AlertType {
    NOTIFICATION,
    ALARM
}
