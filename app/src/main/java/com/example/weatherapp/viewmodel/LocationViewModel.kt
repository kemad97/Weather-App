package com.example.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.location.LocationManager
import android.util.Log

class LocationViewModel(val context: Context) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val myLocation = MutableStateFlow<Pair <Double,Double>?> (null)

    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled

    init {
        checkLocationSettings()

        getLocationUpdates()
    }

    fun checkLocationSettings() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        _isLocationEnabled.value =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

        @SuppressLint("MissingPermission")
      private fun getLocationUpdates() {
        val locRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 300000)
            .build()
        val locCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation

                viewModelScope.launch {
                    myLocation.value = location?.let { Pair(it.latitude, location.longitude) }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locRequest, locCallback, null)

    }


}
