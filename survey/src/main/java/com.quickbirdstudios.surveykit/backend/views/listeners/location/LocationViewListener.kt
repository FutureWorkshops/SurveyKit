package com.quickbirdstudios.surveykit.backend.views.listeners.location

interface LocationViewListener {

    fun onLocationResult(location: Location)

}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
)