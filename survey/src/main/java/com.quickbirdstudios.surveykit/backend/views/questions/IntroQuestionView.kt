package com.quickbirdstudios.surveykit.backend.views.questions

import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.backend.views.step.ViewOrientation
import com.quickbirdstudios.surveykit.result.question_results.EmptyQuestionResult

class IntroQuestionView(
    id: StepIdentifier,
    isOptional: Boolean = false,
    title: String?,
    text: String?,
    startButtonText: String
) : QuestionView(
    id = id,
    isOptional = isOptional,
    title = title,
    text = text,
    nextButtonText = startButtonText,
    buttonOrientation = ViewOrientation.END
) {

    //region Overrides

    override fun createResults() = EmptyQuestionResult(id, startDate)

    override fun isValidInput() = true

    //endregion
}
