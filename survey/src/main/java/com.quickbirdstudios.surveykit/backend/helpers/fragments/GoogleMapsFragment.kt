package com.quickbirdstudios.surveykit.backend.helpers.fragments

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationFragmentListener
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationViewListener
import com.quickbirdstudios.surveykit.backend.views.questions.LocationView

private const val LOCATION_PERMISSION_RESULT = 9999

/**
 * Helper class that implements [LocationFragmentListener] and [OnMapReadyCallback] to make easier
 * to use Fragments that support [LocationView]
 */
abstract class GoogleMapsFragment :
    Fragment(),
    OnMapReadyCallback,
    LocationFragmentListener {

    private var mapView: MapView? = null
    private var locationViewListener: LocationViewListener? = null

    // region LocationFragmentListener callbacks

    override fun setUpMapView(mapView: MapView, locationViewListener: LocationViewListener) {
        this.mapView = mapView
        this.locationViewListener = locationViewListener
        mapView.onCreate(null)
        mapView.getMapAsync(this)
    }

    override fun clearMapView() {
        mapView = null
        locationViewListener = null
    }

    override fun checkLocationPermission() {
        context?.let { safeContext ->
            if (checkSelfPermission(safeContext, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                    requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_PERMISSION_RESULT)
                } else {
                    showToast(R.string.location_permission_denied_permanently)
                }
            } else {
                locationViewListener?.onLocationPermissionGranted()
            }
        }
    }

    // endregion

    private fun showToast(@StringRes stringId: Int) {
        context?.let {
            Toast.makeText(it, getString(stringId), Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_RESULT) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                checkLocationPermission()
            } else {
                showToast(R.string.permission_not_granted)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mapView?.onResume()
        locationViewListener?.onMapReady(map)
    }

    // region Lifecycle events

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

    // endregion

}