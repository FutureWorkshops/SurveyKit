package com.quickbirdstudios.surveykit.steps

import android.content.Context
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.BooleanAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.CurrencyAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.DateAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.EmailAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.ImageSelectorFormat
import com.quickbirdstudios.surveykit.AnswerFormat.LocationAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.MultipleChoiceAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.NumberAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.ScaleAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.SingleChoiceAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.TextAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.TimeAnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.ValuePickerAnswerFormat
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationFragmentListener
import com.quickbirdstudios.surveykit.backend.views.questions.BooleanQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.CurrencyView
import com.quickbirdstudios.surveykit.backend.views.questions.DatePickerQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.EmailQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.ImageSelectorQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.LocationView
import com.quickbirdstudios.surveykit.backend.views.questions.MultipleChoiceQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.NumberQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.ScaleQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.SingleChoiceQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.TextQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.TimePickerQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.ValuePickerQuestionView
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.result.question_results.BooleanQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.DateQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.EmailQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.ImageSelectorResult
import com.quickbirdstudios.surveykit.result.question_results.MultipleChoiceQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.NumberQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.ScaleQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.SingleChoiceQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.TextQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.TimeQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.ValuePickerQuestionResult
import com.quickbirdstudios.surveykit.services.MobileWorkflowServices
import java.io.IOException

class QuestionStep(
    val title: String?,
    val text: String,
    val nextButton: String = "Next",
    val answerFormat: AnswerFormat,
    val locationFragmentListener: LocationFragmentListener? = null,
    override var isOptional: Boolean = false,
    override val id: StepIdentifier = StepIdentifier(),
    override val uuid: String
) : Step {

    //region Public API

    override fun createView(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ): QuestionView =
        when (answerFormat) {
            is TextAnswerFormat -> createTextQuestion(context, stepResult, mobileWorkflowServices)
            is SingleChoiceAnswerFormat -> createSingleChoiceQuestion(context, stepResult, mobileWorkflowServices)
            is MultipleChoiceAnswerFormat -> createMultipleChoiceQuestion(context, stepResult, mobileWorkflowServices)
            is ScaleAnswerFormat -> createScaleQuestion(context, stepResult, mobileWorkflowServices)
            is NumberAnswerFormat -> createNumberQuestion(context, stepResult, mobileWorkflowServices)
            is BooleanAnswerFormat -> createBooleanQuestion(context, stepResult, mobileWorkflowServices)
            is ValuePickerAnswerFormat -> createValuePickerQuestion(context, stepResult, mobileWorkflowServices)
            is DateAnswerFormat -> createDatePickerQuestion(context, stepResult, mobileWorkflowServices)
            is TimeAnswerFormat -> createTimePickerQuestion(context, stepResult, mobileWorkflowServices)
            is EmailAnswerFormat -> createEmailQuestion(context, stepResult, mobileWorkflowServices)
            is ImageSelectorFormat -> createImageSelectorQuestion(context, stepResult, mobileWorkflowServices)
            is LocationAnswerFormat -> createLocationQuestion(context, stepResult, mobileWorkflowServices)
            is CurrencyAnswerFormat -> createCurrencyQuestion(context, stepResult, mobileWorkflowServices)
        }

    //endregion

    //region Private API

    private fun createTextQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        TextQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            isOptional = isOptional,
            answerFormat = this.answerFormat as TextAnswerFormat,
            preselected = stepResult.toSpecificResult<TextQuestionResult>()?.answer
        )

    private fun createSingleChoiceQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ): SingleChoiceQuestionView {
        var format = this.answerFormat as SingleChoiceAnswerFormat
        format = format.copy(textChoices = format.textChoices.map { it.translate(mobileWorkflowServices.localizationService) })

        return SingleChoiceQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = format,
            preselected = stepResult.toSpecificResult<SingleChoiceQuestionResult>()?.answer
        )
    }

    private fun createMultipleChoiceQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ): MultipleChoiceQuestionView {
        var format = this.answerFormat as MultipleChoiceAnswerFormat
        format = format.copy(textChoices = format.textChoices.map { it.translate(mobileWorkflowServices.localizationService) })

        return MultipleChoiceQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = format,
            preselected = stepResult.toSpecificResult<MultipleChoiceQuestionResult>()?.answer
        )
    }

    private fun createScaleQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        ScaleQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as ScaleAnswerFormat,
            preselected = stepResult.toSpecificResult<ScaleQuestionResult>()?.answer
        )

    private fun createNumberQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        NumberQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as NumberAnswerFormat,
            preselected = stepResult.toSpecificResult<NumberQuestionResult>()?.answer
        )

    private fun createBooleanQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        BooleanQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as BooleanAnswerFormat,
            preselected = stepResult.toSpecificResult<BooleanQuestionResult>()?.answer
        )

    private fun createValuePickerQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        ValuePickerQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as ValuePickerAnswerFormat,
            preselected = stepResult.toSpecificResult<ValuePickerQuestionResult>()?.answer
        )

    private fun createDatePickerQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        DatePickerQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as DateAnswerFormat,
            preselected = stepResult.toSpecificResult<DateQuestionResult>()?.answer
        )

    private fun createTimePickerQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        TimePickerQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as TimeAnswerFormat,
            preselected = stepResult.toSpecificResult<TimeQuestionResult>()?.answer
        )

    private fun createEmailQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        EmailQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as EmailAnswerFormat,
            preselected = stepResult.toSpecificResult<EmailQuestionResult>()?.answer
        )

    private fun createImageSelectorQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        ImageSelectorQuestionView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as ImageSelectorFormat,
            preselected = stepResult.toSpecificResult<ImageSelectorResult>()?.answer
        )

    private fun createLocationQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        LocationView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title) ?: "",
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            preselected = stepResult.toSpecificResult(),
            locationFragmentListener = locationFragmentListener
                ?: throw IOException("LocationFragmentListener shouldn't be null for creating a LocationView")
        )

    private fun createCurrencyQuestion(
        context: Context,
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        CurrencyView(
            context = context,
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title) ?: "",
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            preselected = stepResult.toSpecificResult(),
            currencyAnswerFormat = this.answerFormat as CurrencyAnswerFormat
        )

    //endregion

    //region Private Helper

    @Suppress("UNCHECKED_CAST")
    private fun <R : QuestionResult> StepResult?.toSpecificResult(): R? =
        (this?.results?.firstOrNull() as? R)

    //endregion
}
