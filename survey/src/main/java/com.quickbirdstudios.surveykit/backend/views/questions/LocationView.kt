package com.quickbirdstudios.surveykit.backend.views.questions

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.listeners.location.Location
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationFragmentListener
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationViewListener
import com.quickbirdstudios.surveykit.backend.views.question_parts.LocationPart
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.question_results.LocationResult
import kotlinx.android.synthetic.main.location_step.view.*
import java.util.*

internal class LocationView(
    context: Context,
    id: StepIdentifier,
    isOptional: Boolean,
    title: String,
    text: String,
    nextButtonText: String,
    private val preselected: LocationResult?,
    private val locationFragmentListener: LocationFragmentListener
) : QuestionView(context, id, isOptional, title, text, nextButtonText),
    LocationViewListener {

    private val fusedLocationProviderClient = FusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())
    private var map: GoogleMap? = null

    private lateinit var locationPart: LocationPart
    private var location: Location? = null

    override fun createResults(): QuestionResult {
        locationFragmentListener.clearMapView()
        // TODO: Build result
        return LocationResult(
            stringIdentifier = location?.address ?: "",
            id = id,
            startDate = startDate,
            endDate = Date(),
            latitude = null, // TODO: Change null
            longitude = null, // TODO: Change null
            address = null // TODO: Change null
        )
    }

    override fun isValidInput(): Boolean = location != null

    override fun setupViews() {
        super.setupViews()
        locationPart = LocationPart(context)

        with(locationPart) {
            // TODO: Add text change listener
//            view.addressEt.doAfterTextChanged { editable ->
//                editable?.toString()?.let { address ->
//                    setMarkerFromAddress(address)
//                }
//            }

            content.add(this)
            locationFragmentListener.setUpMapView(view.mapView, this@LocationView)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        locationFragmentListener.checkLocationPermission()
    }

    @SuppressLint("MissingPermission")
    override fun onLocationPermissionGranted() {
        map?.isMyLocationEnabled = true
        map?.setOnMyLocationButtonClickListener {
            getDeviceLocation()
            true
        }
        getDeviceLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val locationResult = fusedLocationProviderClient.lastLocation

        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { lastKnownLocation ->
                    with(lastKnownLocation) {
                        moveCameraAndAddMarker(latitude, longitude)
                        val address = getAddressFromLatLng(latitude, longitude)

                        location = Location(latitude, longitude, address)
                        locationPart.view.addressEt.setText(location?.address)
                        locationPart.view.addressEt.setSelection(location?.address?.length ?: 0)
                    }
                }
            }
        }
    }

    private fun moveCameraAndAddMarker(latitude: Double, longitude: Double) {
        map?.clear()
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 17f))
        map?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return addresses[0].getAddressLine(0)
    }

    private fun setMarkerFromAddress(address: String) {
        val addresses = geocoder.getFromLocationName(address, 1)
        moveCameraAndAddMarker(addresses[0].latitude, addresses[0].longitude)
    }

}