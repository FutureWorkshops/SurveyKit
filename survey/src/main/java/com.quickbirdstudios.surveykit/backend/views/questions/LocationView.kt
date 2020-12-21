package com.quickbirdstudios.surveykit.backend.views.questions

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    id: StepIdentifier,
    isOptional: Boolean,
    title: String,
    text: String,
    nextButtonText: String,
    private val preselected: LocationResult?,
    private val locationFragmentListener: LocationFragmentListener
) : QuestionView(id, isOptional, title, text, nextButtonText),
    LocationViewListener {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var map: GoogleMap? = null

    private lateinit var locationPart: LocationPart
    private var location: Location? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fusedLocationProviderClient = FusedLocationProviderClient(context)
        geocoder = Geocoder(context, Locale.getDefault())
    }

    override fun createResults(): QuestionResult {
        locationFragmentListener.clearMapView()
        return LocationResult(
            stringIdentifier = location?.address ?: "",
            id = id,
            startDate = startDate,
            endDate = Date(),
            latitude = location?.latitude,
            longitude = location?.longitude,
            address = location?.address
        )
    }

    override fun isValidInput(): Boolean = location != null

    override fun setupViews() {
        super.setupViews()
        context?.let {
            locationPart = LocationPart(it)

            with(locationPart) {
                view.addressEt.setOnEditorActionListener { view, actionId, _ ->
                    var handled = false
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val address = view.addressEt.text.toString()
                        if (address.isNotEmpty()) setMarkerFromAddress(address)
                        handled = true
                    }

                    hideKeyboard(view)
                    handled
                }

                content.add(this)
                locationFragmentListener.setUpMapView(view.mapView, this@LocationView)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        locationFragmentListener.checkLocationPermission()

        if (preselected?.isValidResult() == true) {
            moveCameraAndAddMarker(
                preselected.latitude!!,
                preselected.longitude!!,
                preselected.address!!
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onLocationPermissionGranted() {
        map?.isMyLocationEnabled = true
        map?.setOnMyLocationButtonClickListener {
            getDeviceLocation()
            true
        }

        if (preselected?.isValidResult() == false || preselected == null) getDeviceLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val locationResult = fusedLocationProviderClient.lastLocation

        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { lastKnownLocation ->
                    with(lastKnownLocation) {
                        val address = getAddressFromLatLng(latitude, longitude)
                        moveCameraAndAddMarker(latitude, longitude, address)
                    }
                }
            }
        }
    }

    private fun moveCameraAndAddMarker(latitude: Double, longitude: Double, address: String?) {
        val zoom = if (address == null) 5f else 15f
        map?.clear()
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))

        address?.let {
            map?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
            location = Location(latitude, longitude, it)
            locationPart.view.addressEt.setText(it)
            locationPart.view.addressEt.setSelection(it.length)
            footer.canContinue = isValidInput()
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses.isNotEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            Toast.makeText(context, "No results for this location", Toast.LENGTH_LONG).show()
            null
        }
    }

    private fun setMarkerFromAddress(address: String) {
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses.isNotEmpty()) {
            moveCameraAndAddMarker(
                addresses[0].latitude,
                addresses[0].longitude,
                address
            )
        } else {
            Toast.makeText(context, "No results for this address", Toast.LENGTH_LONG).show()
        }
    }

}