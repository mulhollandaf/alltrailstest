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
                }
            }

        return location
    }

}