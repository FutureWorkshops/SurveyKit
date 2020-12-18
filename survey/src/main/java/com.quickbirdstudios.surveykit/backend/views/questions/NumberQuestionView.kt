package com.quickbirdstudios.surveykit.backend.views.questions

import android.content.Context
import android.view.Gravity
import androidx.annotation.StringRes
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.helpers.extensions.afterTextChanged
import com.quickbirdstudios.surveykit.backend.views.question_parts.NumberTextFieldPart
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.question_results.NumberQuestionResult

internal class NumberQuestionView(
    id: StepIdentifier,
    isOptional: Boolean,
    title: String?,
    text: String?,
    nextButtonText: String,
    @StringRes private val hintText: Int = R.string.empty,
    private val answerFormat: AnswerFormat.NumberAnswerFormat,
    private val preselected: Long? = null
) : QuestionView(id, isOptional, title, text, nextButtonText) {

    //region Members

    private lateinit var questionAnswerView: NumberTextFieldPart

    //endregion

    //region Overrides

    override fun createResults(): QuestionResult =
        NumberQuestionResult(
            id = id,
            startDate = startDate,
            answer = questionAnswerView.field.text.toString().toLongOrNull(),
            stringIdentifier = questionAnswerView.field.text.toString()
        )

    override fun isValidInput(): Boolean = isOptional || questionAnswerView.field.text.isNotBlank()

    override fun setupViews() {
        super.setupViews()
        context?.let {
            questionAnswerView = content.add(NumberTextFieldPart.withHint(it, hintText))
            questionAnswerView.field.gravity = Gravity.CENTER
            questionAnswerView.field.hint = answerFormat.hint
            questionAnswerView.field.afterTextChanged { footer.canContinue = isValidInput() }
            val alreadyEntered = preselected?.toString() ?: answerFormat.defaultValue?.toString()
            questionAnswerView.field.setText(alreadyEntered ?: "")
        }
    }

    //endregion
}
