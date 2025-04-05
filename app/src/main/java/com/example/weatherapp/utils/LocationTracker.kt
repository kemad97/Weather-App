package com.example.weatherapp.utils

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
import android.content.SharedPreferences

class LocationTracker(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val myLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled

    private val locationPreferences = LocationPreferences(context)


    init {
        myLocation.value = locationPreferences.getLastLocation()
        checkLocationSettings()
        //getLocationUpdates()
    }

    fun getCachedLocation(): Pair<Double, Double>? {
        return locationPreferences.getLastLocation()
    }

    @SuppressLint("ServiceCast")
    fun checkLocationSettings() {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        _isLocationEnabled.value =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates() {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val newLocation = Pair(it.latitude, it.longitude)
                myLocation.value = newLocation
                locationPreferences.saveLastLocation(it.latitude, it.longitude)
            } ?: run {
                // If no last location, use cached location
                myLocation.value = locationPreferences.getLastLocation()
            }
        }

        val locRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
            .setMinUpdateDistanceMeters(1000.0F)
            .setMinUpdateIntervalMillis(2000)
            .build()

        val locCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    myLocation.value = Pair(location.latitude, location.longitude)
                    locationPreferences.saveLastLocation(location.latitude, location.longitude)

                }
            }
        }
        try {
            fusedLocationClient.requestLocationUpdates(locRequest, locCallback, null)
        } catch (e: Exception) {
            // If  fail, g to cached location
            myLocation.value = locationPreferences.getLastLocation()
        }

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




class LocationPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        LOCATION_PREFS, Context.MODE_PRIVATE
    )

    fun saveLastLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat(LAST_LAT, lat.toFloat())
            .putFloat(LAST_LON, lon.toFloat())
            .apply()
    }

    fun getLastLocation(): Pair<Double, Double>? {
        val lat = prefs.getFloat(LAST_LAT, Float.MIN_VALUE)
        val lon = prefs.getFloat(LAST_LON, Float.MIN_VALUE)
        return if (lat != Float.MIN_VALUE && lon != Float.MIN_VALUE) {
            Pair(lat.toDouble(), lon.toDouble())
        } else null
    }

    companion object {
        private const val LOCATION_PREFS = "location_preferences"
        private const val LAST_LAT = "last_latitude"
        private const val LAST_LON = "last_longitude"
    }
}