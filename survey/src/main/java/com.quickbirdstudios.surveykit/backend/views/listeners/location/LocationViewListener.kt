package com.quickbirdstudios.surveykit.backend.views.listeners.location

import com.google.android.gms.maps.GoogleMap

interface LocationViewListener {

    fun onMapReady(map: GoogleMap)

    fun onLocationPermissionGranted()

}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
)