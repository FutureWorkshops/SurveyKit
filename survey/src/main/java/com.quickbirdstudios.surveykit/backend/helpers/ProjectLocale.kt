/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.helpers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProjectLocale(
    @SerializedName("language_id") val languageId: String,
    @SerializedName("translations") val translations: List<Translation>
) : Parcelable

@Parcelize
data class Translation(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String
) : Parcelable