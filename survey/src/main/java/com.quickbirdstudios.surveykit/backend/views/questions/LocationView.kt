package com.quickbirdstudios.surveykit.backend.views.questions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.listeners.location.Location
import com.quickbirdstudios.surveykit.backend.views.question_parts.LocationPart
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.question_results.LocationResult
import kotlinx.android.synthetic.main.location_step.view.*
import java.util.*

private const val LOCATION_PERMISSION_RESULT = 9999

internal class LocationView(
    id: StepIdentifier,
    isOptional: Boolean,
    title: String,
    text: String,
    nextButtonText: String,
    private val preselected: LocationResult?
) : QuestionView(id, isOptional, title, text, nextButtonText),
    OnMapReadyCallback {

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
        clearMapView()
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
                setUpMapView(view.mapView)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("MissingPermission")
    private fun onLocationPermissionGranted() {
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


    // region Location variables
    private var mapView: MapView? = null
    // endregion

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_RESULT -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission()
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    context?.let {
                        val string = getString(R.string.location_permission_denied_permanently)
                        Toast.makeText(it, string, Toast.LENGTH_LONG).show()
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mapView?.onResume()
        this.map = map
        checkLocationPermission()

        if (preselected?.isValidResult() == true) {
            moveCameraAndAddMarker(
                preselected.latitude!!,
                preselected.longitude!!,
                preselected.address!!
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    // region LocationFragmentListener callbacks

    private fun setUpMapView(mapView: MapView) {
        this.mapView = mapView
        mapView.onCreate(null)
        mapView.getMapAsync(this)
    }

    private fun clearMapView() {
        mapView = null
    }

    private fun checkLocationPermission() {
        context?.let { safeContext ->
            if (ActivityCompat.checkSelfPermission(
                    safeContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_RESULT)
            } else {
                onLocationPermissionGranted()
            }
        }
    }

    // endregion

}