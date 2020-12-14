package com.quickbirdstudios.surveykit.backend.views.questions

import android.content.Context
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.step.ViewOrientation
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.question_results.EmptyQuestionResult

class IntroQuestionView(
    context: Context,
    id: StepIdentifier,
    isOptional: Boolean = false,
    title: String?,
    text: String?,
    startButtonText: String
) : QuestionView(context, id, isOptional, title, text, startButtonText, ViewOrientation.END) {

    //region Overrides

    override fun createResults() = EmptyQuestionResult(id, startDate)

    override fun isValidInput() = true

    //endregion
}
