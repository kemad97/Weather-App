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
import kotlinx.coroutines.launch

class LocationViewModel(context: Context) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val myLocation = MutableStateFlow<Pair <Double,Double>?> (null)

    init {
         getLocationUpdates()
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
