package com.quickbirdstudios.surveykit.steps

import android.content.Context
import androidx.lifecycle.LifecycleOwner
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
        mobileWorkflowServices: MobileWorkflowServices,
        lifecycleOwner: LifecycleOwner
    ): QuestionView =
        when (answerFormat) {
            is TextAnswerFormat -> createTextQuestion(stepResult, mobileWorkflowServices)
            is SingleChoiceAnswerFormat -> createSingleChoiceQuestion(stepResult, mobileWorkflowServices)
            is MultipleChoiceAnswerFormat -> createMultipleChoiceQuestion(stepResult, mobileWorkflowServices)
            is ScaleAnswerFormat -> createScaleQuestion(stepResult, mobileWorkflowServices)
            is NumberAnswerFormat -> createNumberQuestion(stepResult, mobileWorkflowServices)
            is BooleanAnswerFormat -> createBooleanQuestion(stepResult, mobileWorkflowServices)
            is ValuePickerAnswerFormat -> createValuePickerQuestion(stepResult, mobileWorkflowServices)
            is DateAnswerFormat -> createDatePickerQuestion(stepResult, mobileWorkflowServices)
            is TimeAnswerFormat -> createTimePickerQuestion(stepResult, mobileWorkflowServices)
            is EmailAnswerFormat -> createEmailQuestion(stepResult, mobileWorkflowServices)
            is ImageSelectorFormat -> createImageSelectorQuestion(stepResult, mobileWorkflowServices)
            is LocationAnswerFormat -> createLocationQuestion(stepResult, mobileWorkflowServices)
            is CurrencyAnswerFormat -> createCurrencyQuestion(stepResult, mobileWorkflowServices)
        }

    //endregion

    //region Private API

    private fun createTextQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        TextQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            isOptional = isOptional,
            answerFormat = this.answerFormat as TextAnswerFormat,
            preselected = stepResult.toSpecificResult<TextQuestionResult>()?.answer
        )

    private fun createSingleChoiceQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ): SingleChoiceQuestionView {
        var format = this.answerFormat as SingleChoiceAnswerFormat
        format =
            format.copy(textChoices = format.textChoices.map { it.translate(mobileWorkflowServices.localizationService) })

        return SingleChoiceQuestionView(
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
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ): MultipleChoiceQuestionView {
        var format = this.answerFormat as MultipleChoiceAnswerFormat
        format =
            format.copy(textChoices = format.textChoices.map { it.translate(mobileWorkflowServices.localizationService) })

        return MultipleChoiceQuestionView(
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
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        ScaleQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as ScaleAnswerFormat,
            preselected = stepResult.toSpecificResult<ScaleQuestionResult>()?.answer
        )

    private fun createNumberQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        NumberQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as NumberAnswerFormat,
            preselected = stepResult.toSpecificResult<NumberQuestionResult>()?.answer
        )

    private fun createBooleanQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        BooleanQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as BooleanAnswerFormat,
            preselected = stepResult.toSpecificResult<BooleanQuestionResult>()?.answer
        )

    private fun createValuePickerQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        ValuePickerQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as ValuePickerAnswerFormat,
            preselected = stepResult.toSpecificResult<ValuePickerQuestionResult>()?.answer
        )

    private fun createDatePickerQuestion(

        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        DatePickerQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as DateAnswerFormat,
            preselected = stepResult.toSpecificResult<DateQuestionResult>()?.answer
        )

    private fun createTimePickerQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        TimePickerQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as TimeAnswerFormat,
            preselected = stepResult.toSpecificResult<TimeQuestionResult>()?.answer
        )

    private fun createEmailQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        EmailQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as EmailAnswerFormat,
            preselected = stepResult.toSpecificResult<EmailQuestionResult>()?.answer
        )

    private fun createImageSelectorQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        ImageSelectorQuestionView(
            id = id,
            title = mobileWorkflowServices.localizationService.getTranslationOrNull(title),
            text = mobileWorkflowServices.localizationService.getTranslation(text),
            isOptional = isOptional,
            nextButtonText = mobileWorkflowServices.localizationService.getTranslation(nextButton),
            answerFormat = this.answerFormat as ImageSelectorFormat,
            preselected = stepResult.toSpecificResult<ImageSelectorResult>()?.answer
        )

    private fun createLocationQuestion(
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        LocationView(
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
        stepResult: StepResult?,
        mobileWorkflowServices: MobileWorkflowServices
    ) =
        CurrencyView(
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
