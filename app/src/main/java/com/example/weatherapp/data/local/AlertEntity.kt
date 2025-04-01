package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts_table")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val startTime: Long,
    val type: AlertType,
    val isEnabled: Boolean = true
)

enum class AlertType {
    NOTIFICATION,
    ALARM
}
