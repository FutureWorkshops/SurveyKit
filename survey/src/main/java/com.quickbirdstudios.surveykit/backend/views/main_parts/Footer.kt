package com.quickbirdstudios.surveykit.backend.views.main_parts

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.backend.helpers.extensions.toColorStateList


class Footer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : Toolbar(context, attrs, defStyleRes), StyleablePart {

    //region Public API

    private var canContinueListener: CanContinueListener? = null
    private var viewId: String = ""

    var themeColor = Color.RED
        set(newColor) {
            buttonContinue.colorMainButtonEnabledState(canContinue, newColor)
            buttonSkip.setTextColor(newColor)
            field = newColor
        }

    var questionCanBeSkipped: Boolean = false
        get() = buttonSkip.visibility != View.VISIBLE
        set(canBeSkipped) {
            buttonSkip.visibility = if (canBeSkipped) View.VISIBLE else View.GONE
            field = canBeSkipped
        }

    var canContinue: Boolean
        get() = buttonContinue.isEnabled
        set(state) {
            buttonContinue.isEnabled = state
            buttonContinue.colorMainButtonEnabledState(state, themeColor)
            canContinueListener?.onContinueChange(viewId, state)
        }

    fun setContinueButtonText(text: String) {
        buttonContinue.text = text
    }

    fun setCanContinueListener(viewId: String, canContinueListener: CanContinueListener) {
        this.viewId = viewId
        this.canContinueListener = canContinueListener
    }

    var onContinue: () -> Unit = {}
    var onSkip: () -> Unit = {}

    fun setButtonsGravity(gravity: Int) {
        buttonContinue.layoutParams = (buttonContinue.layoutParams as LinearLayout.LayoutParams).apply {
            this.gravity = gravity
        }
        buttonSkip.layoutParams = (buttonSkip.layoutParams as LinearLayout.LayoutParams).apply {
            this.gravity = gravity
        }
    }

    //endregion

    //region Members

    private val root: View = View.inflate(context, R.layout.layout_footer, this)
    private val buttonContinue = root.findViewById<Button>(R.id.button_continue).apply {
        setOnClickListener {
            hideKeyboard()
            onContinue()
        }
        colorMainButtonEnabledState(true, themeColor)
    }
    private val buttonSkip = root.findViewById<Button>(R.id.button_skip_question).apply {
        setOnClickListener {
            hideKeyboard()
            onSkip()
        }
        colorSkipButton(themeColor)
    }

    //endregion

    //region Private API

    private fun Button.colorMainButtonEnabledState(enabled: Boolean, color: Int) {
        backgroundTintList = if (enabled) {
            color.toColorStateList()
        } else {
            resources.getColor(R.color.disabled_grey, null).toColorStateList()
        }
    }

    private fun Button.colorSkipButton(color: Int) {
        setTextColor(color)
        backgroundTintList = color.toColorStateList()
    }

    // TODO this should probably not be done here
    private fun hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    //endregion

    //region Overrides

    override fun style(surveyTheme: SurveyTheme) {
        themeColor = surveyTheme.themeColor
    }

    //endregion
}
