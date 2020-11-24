package com.quickbirdstudios.surveykit.steps

import android.content.Context
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.AnswerFormat.*
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.helpers.DictionaryHelper
import com.quickbirdstudios.surveykit.backend.views.listeners.location.LocationFragmentListener
import com.quickbirdstudios.surveykit.backend.views.questions.BooleanQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.CurrencyView
import com.quickbirdstudios.surveykit.backend.views.questions.DatePickerQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.EmailQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.ImageSelectorQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.NumberQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.MultipleChoiceQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.ScaleQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.SingleChoiceQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.TextQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.TimePickerQuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.ValuePickerQuestionView
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.backend.views.questions.LocationView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.result.question_results.BooleanQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.DateQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.EmailQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.ImageSelectorResult
import com.quickbirdstudios.surveykit.result.question_results.NumberQuestionResult
import com.quickbirdstudios.surveykit.result.question_results.MultipleChoiceQuestionResult
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
    val nextButton: String = DictionaryHelper.getTranslation("Next"),
    val answerFormat: AnswerFormat,
    val locationFragmentListener: LocationFragmentListener? = null,
    override var isOptional: Boolean = false,
    override val id: StepIdentifier = StepIdentifier(),
    override val uuid: String
) : Step {

    //region Public API

    override fun createView(context: Context, stepResult: StepResult?, mobileWorkflowServices: MobileWorkflowServices): QuestionView =
        when (answerFormat) {
            is TextAnswerFormat -> createTextQuestion(context, stepResult)
            is SingleChoiceAnswerFormat -> createSingleChoiceQuestion(context, stepResult)
            is MultipleChoiceAnswerFormat -> createMultipleChoiceQuestion(context, stepResult)
            is ScaleAnswerFormat -> createScaleQuestion(context, stepResult)
            is NumberAnswerFormat -> createNumberQuestion(context, stepResult)
            is BooleanAnswerFormat -> createBooleanQuestion(context, stepResult)
            is ValuePickerAnswerFormat -> createValuePickerQuestion(context, stepResult)
            is DateAnswerFormat -> createDatePickerQuestion(context, stepResult)
            is TimeAnswerFormat -> createTimePickerQuestion(context, stepResult)
            is EmailAnswerFormat -> createEmailQuestion(context, stepResult)
            is ImageSelectorFormat -> createImageSelectorQuestion(context, stepResult)
            is LocationAnswerFormat -> createLocationQuestion(context, stepResult)
            is CurrencyAnswerFormat -> createCurrencyQuestion(context, stepResult)
        }

    //endregion

    //region Private API

    private fun createTextQuestion(context: Context, stepResult: StepResult?) =
        TextQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            nextButtonText = nextButton,
            isOptional = isOptional,
            answerFormat = this.answerFormat as TextAnswerFormat,
            preselected = stepResult.toSpecificResult<TextQuestionResult>()?.answer
        )

    private fun createSingleChoiceQuestion(context: Context, stepResult: StepResult?) =
        SingleChoiceQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as SingleChoiceAnswerFormat,
            preselected = stepResult.toSpecificResult<SingleChoiceQuestionResult>()?.answer
        )

    private fun createMultipleChoiceQuestion(context: Context, stepResult: StepResult?) =
        MultipleChoiceQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as MultipleChoiceAnswerFormat,
            preselected = stepResult.toSpecificResult<MultipleChoiceQuestionResult>()?.answer
        )

    private fun createScaleQuestion(context: Context, stepResult: StepResult?) =
        ScaleQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as ScaleAnswerFormat,
            preselected = stepResult.toSpecificResult<ScaleQuestionResult>()?.answer
        )

    private fun createNumberQuestion(context: Context, stepResult: StepResult?) =
        NumberQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as NumberAnswerFormat,
            preselected = stepResult.toSpecificResult<NumberQuestionResult>()?.answer
        )

    private fun createBooleanQuestion(context: Context, stepResult: StepResult?) =
        BooleanQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as BooleanAnswerFormat,
            preselected = stepResult.toSpecificResult<BooleanQuestionResult>()?.answer
        )

    private fun createValuePickerQuestion(context: Context, stepResult: StepResult?) =
        ValuePickerQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as ValuePickerAnswerFormat,
            preselected = stepResult.toSpecificResult<ValuePickerQuestionResult>()?.answer
        )

    private fun createDatePickerQuestion(context: Context, stepResult: StepResult?) =
        DatePickerQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as DateAnswerFormat,
            preselected = stepResult.toSpecificResult<DateQuestionResult>()?.answer
        )

    private fun createTimePickerQuestion(context: Context, stepResult: StepResult?) =
        TimePickerQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as TimeAnswerFormat,
            preselected = stepResult.toSpecificResult<TimeQuestionResult>()?.answer
        )

    private fun createEmailQuestion(context: Context, stepResult: StepResult?) =
        EmailQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as EmailAnswerFormat,
            preselected = stepResult.toSpecificResult<EmailQuestionResult>()?.answer
        )

    private fun createImageSelectorQuestion(context: Context, stepResult: StepResult?) =
        ImageSelectorQuestionView(
            context = context,
            id = id,
            title = title,
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            answerFormat = this.answerFormat as ImageSelectorFormat,
            preselected = stepResult.toSpecificResult<ImageSelectorResult>()?.answer
        )

    private fun createLocationQuestion(context: Context, stepResult: StepResult?) =
        LocationView(
            context = context,
            id = id,
            title = title ?: "",
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
            preselected = stepResult.toSpecificResult(),
            locationFragmentListener = locationFragmentListener
                ?: throw IOException("LocationFragmentListener shouldn't be null for creating a LocationView")
        )

    private fun createCurrencyQuestion(context: Context, stepResult: StepResult?) =
        CurrencyView(
            context = context,
            id = id,
            title = title ?: "",
            text = text,
            isOptional = isOptional,
            nextButtonText = nextButton,
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
