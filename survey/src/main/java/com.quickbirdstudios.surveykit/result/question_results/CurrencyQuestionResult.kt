package com.quickbirdstudios.surveykit.result.question_results

import android.os.Parcelable
import com.quickbirdstudios.surveykit.Identifier
import com.quickbirdstudios.surveykit.result.QuestionResult
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
data class CurrencyQuestionResult(
    override val stringIdentifier: String,
    override val id: Identifier,
    override val startDate: Date,
    override val endDate: Date,
    val currencyCode: String,
    val value: BigDecimal?
) : QuestionResult, Parcelable