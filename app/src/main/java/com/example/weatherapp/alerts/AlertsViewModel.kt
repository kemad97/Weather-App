package com.example.weatherapp.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.repo.WeatherRepository
import com.example.weatherapp.model.local.AlertType
import com.example.weatherapp.model.local.AlertEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _alerts = MutableStateFlow<List<AlertEntity>>(emptyList())
    val alerts: StateFlow<List<AlertEntity>> = _alerts

    init {
        getAlerts()
    }

    private fun getAlerts() {
        viewModelScope.launch {
            repository.getAllAlerts().collect {
                _alerts.value = it
            }
        }
    }

    fun addAlert(startTime: Long, type: AlertType, title: String) {
        viewModelScope.launch {
            val alert = AlertEntity(
                title = title,
                startTime = startTime,
                type = type
            )
            repository.insertAlert(alert)

        }
    }


    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }

    class AlertsViewModelFactory(private val repository: WeatherRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
                return AlertsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown Alert ViewModel class")
        }

    }
}
