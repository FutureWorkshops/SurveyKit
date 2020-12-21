package com.quickbirdstudios.surveykit.backend.views.questions

import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.question_parts.QuestionAnimation
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.question_results.FinishQuestionResult
import com.quickbirdstudios.surveykit.steps.CompletionStep

internal class FinishQuestionView(
    id: StepIdentifier = StepIdentifier(),
    title: String?,
    text: String?,
    finishButtonText: String,
    private val lottieAnimation: CompletionStep.LottieAnimation?,
    private val repeatCount: Int?
) : QuestionView(id, false, title, text, finishButtonText) {

    //region Overrides

    override fun createResults() =
        FinishQuestionResult(id, startDate)

    override fun isValidInput() = true

    override fun setupViews() {
        super.setupViews()
        context?.let {
            content.add(
                QuestionAnimation(
                    context = it,
                    animation = lottieAnimation,
                    repeatCount = repeatCount
                )
            )

            footer.questionCanBeSkipped = false
            footer.onContinue = { onCloseListener(createResults(), FinishReason.Completed) }
        }
    }

    //endregion
}
