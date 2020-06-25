package com.quickbirdstudios.surveykit.backend.views.listeners.location

import com.google.android.gms.maps.MapView

interface LocationFragmentListener {

    fun setUpMapView(mapView: MapView, locationViewListener: LocationViewListener)

    fun clearMapView()

    fun requestCurrentLocation()

    fun requestAddressInformation(address: String)

}