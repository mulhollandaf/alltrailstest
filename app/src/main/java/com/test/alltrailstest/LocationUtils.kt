package com.test.alltrailstest

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import timber.log.Timber


class LocationUtils{

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location : MutableLiveData<Location> = MutableLiveData()

    fun getInstance(appContext: Context): FusedLocationProviderClient{
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
        return fusedLocationProviderClient
    }

    @SuppressLint("MissingPermission")
    fun getLocation() : LiveData<Location> {
        Timber.d("GET Location")
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    Timber.d("SUCCESS Location $loc")
                    location.value = loc
                } else {
                    requestLocation()
                }
            }

        return location
    }

    //TODO This doesn't really work
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        Timber.d("Requesting Location")
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000
        locationRequest.fastestInterval = 5 * 1000
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationProviderClient.removeLocationUpdates(this)
                for (loc in locationResult.locations) {
                    if (loc != null) {
                        Timber.d("Location result is $loc")
                        location.value = loc
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

}