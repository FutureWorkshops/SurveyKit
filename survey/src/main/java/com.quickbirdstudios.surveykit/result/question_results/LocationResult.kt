package com.quickbirdstudios.surveykit.result.question_results

import android.os.Parcelable
import com.quickbirdstudios.surveykit.Identifier
import com.quickbirdstudios.surveykit.result.QuestionResult
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class LocationResult(
    override val stringIdentifier: String,
    override val id: Identifier,
    override val startDate: Date,
    override val endDate: Date,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?
) : QuestionResult, Parcelable {

    fun isValidResult(): Boolean = latitude != null && longitude != null && address != null

}