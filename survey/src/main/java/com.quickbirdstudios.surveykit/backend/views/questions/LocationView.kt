package com.quickbirdstudios.surveykit.backend.views.questions

import android.content.Context
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.listeners.location.Location
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationFragmentListener
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationViewListener
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.backend.views.question_parts.LocationPart
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
        locationPart =
            LocationPart(
                context
            )

        with(locationPart) {
            // TODO: Add text change listener
//            view.addressEt.doAfterTextChanged { editable ->
//                editable?.toString()?.let { address ->
//                    locationFragmentListener.requestAddressInformation(address, this@LocationView)
//                }
//            }

            content.add(this)
            locationFragmentListener.setUpMapView(view.mapView, this@LocationView)
        }
    }

    override fun onLocationResult(location: Location) {
        this.location = location
        // TODO: Set result in edit text
    }

}