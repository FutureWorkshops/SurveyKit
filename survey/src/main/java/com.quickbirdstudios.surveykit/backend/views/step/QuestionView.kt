package com.quickbirdstudios.surveykit.backend.views.step

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.CallSuper
import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.domain.ImageContentMode
import com.quickbirdstudios.surveykit.backend.helpers.extensions.px
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.backend.views.main_parts.CanContinueListener
import com.quickbirdstudios.surveykit.backend.views.main_parts.Content
import com.quickbirdstudios.surveykit.backend.views.main_parts.Dialogs
import com.quickbirdstudios.surveykit.backend.views.main_parts.Footer
import com.quickbirdstudios.surveykit.backend.views.main_parts.Header
import com.quickbirdstudios.surveykit.backend.views.question_parts.InfoTextPart
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.services.image_loader.ImageLoaderService
import java.util.*

abstract class QuestionView(
    id: StepIdentifier,
    isOptional: Boolean,
    override val title: String?,
    private val text: String?,
    private val nextButtonText: String,
    private val imageUrl: String? = null,
    private val imageContentMode: ImageContentMode? = null,
    private val buttonOrientation: ViewOrientation = ViewOrientation.CENTER,
    private val textOrientation: ViewOrientation = ViewOrientation.CENTER
) : StepView(title, id, isOptional), ViewActions {

    //region Members

    private lateinit var root: View
    private lateinit var canContinueListener: CanContinueListener
    lateinit var header: Header
    lateinit var content: Content
    lateinit var footer: Footer
    private var abortDialogConfiguration: AbortDialogConfiguration? = null

    val startDate: Date = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.view_question, container, false)
        return root
    }

    fun setFormItem(canContinueListener: CanContinueListener) {
        this.canContinueListener = canContinueListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        header = root.findViewById(R.id.questionHeader)
        content = root.findViewById(R.id.questionContent)
        footer = content.findViewById(R.id.questionFooter)
        setUpToolbar(root.findViewById(R.id.toolbar))
        setupViews()
        onViewCreated()

        if (::canContinueListener.isInitialized) {
            header.visibility = View.GONE
            content.hideFooterContainer()
            canContinueListener.registerId(id.id, isValidInput())
            footer.setCanContinueListener(id.id, canContinueListener)
            val marginInDp = resources.getDimension(R.dimen.form_view_vertical_margin).toInt()
            content.changeContainerVerticalMargins(marginInDp)
        }
    }

    //endregion

    //region Overrides

    //endregion

    //region Abstracts

    abstract override fun createResults(): QuestionResult

    abstract override fun isValidInput(): Boolean

    //endregion

    //region Open Helpers

    @CallSuper
    override fun setupViews() {
        context?.let { safeContext ->
            imageUrl?.let {
                val displayMetrics = resources.displayMetrics
                val dpHeight = (displayMetrics.heightPixels / displayMetrics.density)
                val pxHeight = safeContext.px(dpHeight)
                val imageView = ImageView(safeContext).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
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

            title?.let { InfoTextPart.title(safeContext, it, textOrientation) }?.let(content::add)
            text?.let { InfoTextPart.info(safeContext, it, textOrientation) }?.let(content::add)

            header.onBack = { onBackListener(createResults()) }
            // TODO add translations and move out of this class
            header.onCancel = {
                Dialogs.cancel(
                    safeContext,
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
    }

    override fun onViewCreated() {
        super.onViewCreated()

        footer.canContinue = isValidInput()
        footer.onContinue = { onNextListener(createResults()) }
        footer.onSkip = { onSkipListener() }
        footer.questionCanBeSkipped = isOptional
        footer.setContinueButtonText(nextButtonText)

        header.style(surveyTheme)
        content.style(surveyTheme)
        abortDialogConfiguration = surveyTheme.abortDialogConfiguration
    }

//endregion
}
