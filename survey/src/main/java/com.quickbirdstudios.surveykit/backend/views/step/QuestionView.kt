package com.quickbirdstudios.surveykit.backend.views.step

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.backend.domain.ImageContentMode
import com.quickbirdstudios.surveykit.backend.helpers.extensions.px
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.backend.views.main_parts.Content
import com.quickbirdstudios.surveykit.backend.views.main_parts.Dialogs
import com.quickbirdstudios.surveykit.backend.views.main_parts.Footer
import com.quickbirdstudios.surveykit.backend.views.main_parts.Header
import com.quickbirdstudios.surveykit.backend.views.question_parts.InfoTextPart
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.services.image_loader.ImageLoaderService
import java.util.*

abstract class QuestionView(
    context: Context,
    id: StepIdentifier,
    isOptional: Boolean,
    override val title: String?,
    private val text: String?,
    private val nextButtonText: String,
    private val imageUrl: String? = null,
    private val imageContentMode: ImageContentMode? = null,
    private val buttonOrientation: ViewOrientation = ViewOrientation.CENTER,
    private val textOrientation: ViewOrientation = ViewOrientation.CENTER
) : StepView(context, title, id, isOptional), ViewActions {

    //region Members

    private val root: View = View.inflate(context, R.layout.view_question, this)
    var header: Header = root.findViewById(R.id.questionHeader)
    var content: Content = root.findViewById(R.id.questionContent)
    var footer: Footer = content.findViewById(R.id.questionFooter)
    private var abortDialogConfiguration: AbortDialogConfiguration? = null

    val startDate: Date = Date()

    //endregion

    //region Overrides

    override fun style(surveyTheme: SurveyTheme) {
        header.style(surveyTheme)
        content.style(surveyTheme)
        abortDialogConfiguration = surveyTheme.abortDialogConfiguration
    }

    //endregion

    //region Abstracts

    abstract override fun createResults(): QuestionResult

    abstract override fun isValidInput(): Boolean

    //endregion

    //region Open Helpers

    @CallSuper
    override fun setupViews() {
        imageUrl?.let {
            val displayMetrics = resources.displayMetrics
            val dpHeight = (displayMetrics.heightPixels / displayMetrics.density)
            val pxHeight = context.px(dpHeight)
            val imageView = ImageView(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (pxHeight * 0.45).toInt()
                ).apply {
                    setMargins(0, 0, 0, (pxHeight * 0.05).toInt())
                }

                scaleType = when (imageContentMode) {
                    ImageContentMode.FILL -> ImageView.ScaleType.FIT_XY
                    ImageContentMode.ASPECT_FIT -> ImageView.ScaleType.CENTER_INSIDE
                    ImageContentMode.ASPECT_FILL,
                    null -> ImageView.ScaleType.CENTER_CROP
                }
            }

            content.add(imageView)
            ImageLoaderService().loadImageFromUrl(imageUrl, imageView)
        }

        title?.let { InfoTextPart.title(context, it, textOrientation) }?.let(content::add)
        text?.let { InfoTextPart.info(context, it, textOrientation) }?.let(content::add)

        header.onBack = { onBackListener(createResults()) }
        // TODO add translations and move out of this class
        header.onCancel = {
            Dialogs.cancel(
                context,
                AbortDialogConfiguration(
                    abortDialogConfiguration?.title ?: R.string.abort_dialog_title,
                    abortDialogConfiguration?.message ?: R.string.abort_dialog_message,
                    abortDialogConfiguration?.neutralMessage
                        ?: R.string.abort_dialog_neutral_message,
                    abortDialogConfiguration?.negativeMessage ?: R.string.abort_dialog_neutral_message
                )
            ) {
                onCloseListener(createResults(), FinishReason.Discarded)
            }
        }

        footer.setButtonsGravity(buttonOrientation.gravity)
    }

    override fun onViewCreated() {
        super.onViewCreated()

        footer.canContinue = isValidInput()
        footer.onContinue = { onNextListener(createResults()) }
        footer.onSkip = { onSkipListener() }
        footer.questionCanBeSkipped = isOptional
        footer.setContinueButtonText(nextButtonText)
    }

//endregion
}
