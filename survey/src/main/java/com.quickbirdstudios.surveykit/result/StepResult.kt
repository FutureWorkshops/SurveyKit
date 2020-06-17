package com.quickbirdstudios.surveykit.result

import android.os.Parcelable
import com.quickbirdstudios.surveykit.StepIdentifier
import java.util.Date
import kotlinx.android.parcel.Parcelize

@Parcelize
open class StepResult(
    override val id: StepIdentifier,
    override val startDate: Date,
    override val endDate: Date = Date(),
    open val results: MutableList<QuestionResult> = mutableListOf()
) : Result, Parcelable
