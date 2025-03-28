package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationTracker(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val myLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled

    init {
        checkLocationSettings()
        getLocationUpdates()
    }

    @SuppressLint("ServiceCast")
    fun checkLocationSettings() {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        _isLocationEnabled.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
     fun getLocationUpdates() {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                myLocation.value = Pair(it.latitude, it.longitude)
            }
        }

        val locRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateDistanceMeters(1000.0F)
            .setMinUpdateIntervalMillis(2000)
            .build()

        val locCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    myLocation.value = Pair(location.latitude, location.longitude)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locRequest, locCallback, null)
    }

    companion object {
        @Volatile
        private var instance: LocationTracker? = null

        fun getInstance(context: Context): LocationTracker {
            return instance ?: synchronized(this) {
                instance ?: LocationTracker(context.applicationContext).also { instance = it }
            }
        }
    }
}