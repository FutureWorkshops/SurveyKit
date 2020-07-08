package com.quickbirdstudios.surveykit.backend.views.question_parts

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.TextChoice
import com.quickbirdstudios.surveykit.backend.helpers.extensions.px
import com.quickbirdstudios.surveykit.backend.views.main_parts.StyleablePart
import com.quickbirdstudios.surveykit.backend.views.question_parts.helper.BackgroundDrawable
import com.quickbirdstudios.surveykit.backend.views.question_parts.helper.BackgroundDrawable.Border.Both
import com.quickbirdstudios.surveykit.backend.views.question_parts.helper.BackgroundDrawable.Border.Bottom
import com.quickbirdstudios.surveykit.backend.views.question_parts.helper.createSelectableThemedBackground

private const val EDIT_TEXT_TAG = "EDIT_TEXT_TAG"

internal class SingleChoicePart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RadioGroup(context, attrs), StyleablePart {

    //region Public API

    @ColorInt
    var themeColor: Int = ContextCompat.getColor(context, R.color.cyan_normal)
        set(color) {
            fields.forEachIndexed { index, radioButton ->
                val border = when (options[index]) {
                    is TextChoice.Normal -> if (index == 0) Both else Bottom
                    is TextChoice.Other -> BackgroundDrawable.Border.None
                }
                radioButton.background = radioButton.createSelectableThemedBackground(context, border, color)
            }
            field = color
        }

    @ColorInt
    var radioButtonTextColor: Int = ContextCompat.getColor(context, R.color.cyan_normal)
        set(color) {
            fields.forEach { it.setTextColor(defaultColor) }
            this.findViewWithTag<RadioButton>(options.indexOf(selected))?.setTextColor(color)
            field = color
        }

    @ColorInt
    var defaultColor: Int = Color.BLACK

    var options: List<TextChoice> = emptyList()
        set(value) {
            update(value)
            field = value
        }

    var selected: TextChoice?
        get() = selectedChoice()
        set(choice) {
            if (choice != null) {
                this.findViewWithTag<RadioButton>(options.indexOf(choice))?.isChecked = true
                if (choice is TextChoice.Other) {
                    val editText = this.findViewWithTag<EditText>(EDIT_TEXT_TAG)
                    editText?.setText(choice.result)
                }
            }
        }

    fun isOneSelected(): Boolean {
        val hasValidOtherResult = if (selected is TextChoice.Other) {
            this.findViewWithTag<EditText>(EDIT_TEXT_TAG)?.text?.isNotEmpty() == true
        } else {
            true
        }

        return this.checkedRadioButtonId != -1 && hasValidOtherResult
    }

    var onCheckedChangeListener: (RadioGroup, Int) -> Unit = { _, _ -> }

    var onTextChangedListener: (String) -> Unit = { }

    //endregion

    //region Overrides

    override fun style(surveyTheme: SurveyTheme) {
        themeColor = surveyTheme.themeColor
        radioButtonTextColor = surveyTheme.textColor
    }

    //endregion

    //region Private API

    private val fields: List<RadioButton>
        get() = (0 until this.childCount).mapNotNull { this.getChildAt(it) as? RadioButton }

    private fun update(list: List<TextChoice>) {
        val selectedChoice = selected
        this.removeAllViews()

        list.forEachIndexed { index, textChoice ->
            val isSelected = textChoice == selectedChoice
            val border = if (index == 0) Both else Bottom

            when (textChoice) {
                is TextChoice.Normal -> {
                    val radioButton = createRadioButton(textChoice.text, index, border, isSelected)
                    this.addView(radioButton)
                }
                is TextChoice.Other -> {
                    val (radioButton, editText) = otherRadioButton(
                        textChoice.text,
                        index,
                        isSelected,
                        textChoice.detailText
                    )
                    this.addView(radioButton)
                    this.addView(editText)
                }
            }

        }
    }

    private fun selectedChoice(): TextChoice? {
        val radioButtonIndex: Int = this
            .findViewById<RadioButton>(this.checkedRadioButtonId)?.tag as? Int
            ?: return null

        var textChoice = options[radioButtonIndex]
        textChoice = when (textChoice) {
            is TextChoice.Normal -> textChoice
            is TextChoice.Other -> {
                val result = this.findViewWithTag<EditText>(EDIT_TEXT_TAG)?.text?.toString() ?: ""
                textChoice.result = result
                textChoice
            }
        }

        return textChoice
    }

    private fun selectedRadioButton(): RadioButton? = this.findViewById(this.checkedRadioButtonId)

    //endregion

    //region Internal listeners

    private val internalCheckedChangeListener: (RadioGroup, Int) -> Unit = { group, checkedId ->
        fields.forEach { it.setTextColor(defaultColor) }
        selectedRadioButton()?.setTextColor(radioButtonTextColor)
        onCheckedChangeListener(group, checkedId)
    }

    //endregion

    //region RadioButton Creation Helpers

    private fun createRadioButton(
        label: String,
        tag: Int,
        border: BackgroundDrawable.Border,
        isSelected: Boolean
    ): RadioButton {
        val verticalPaddingEditText = context.px(
            context.resources.getDimension(R.dimen.text_field_vertical_padding)
        ).toInt()
        val horizontalPaddingEditTextLeft = context.px(
            context.resources.getDimension(R.dimen.text_field_horizontal_padding_left)
        ).toInt()
        val horizontalPaddingEditTextRight = context.px(
            context.resources.getDimension(R.dimen.text_field_horizontal_padding_right)
        ).toInt()

        return RadioButton(context).apply {
            id = View.generateViewId()
            text = label
            this.tag = tag
            isFocusable = true
            isClickable = true
            buttonDrawable = null
            textSize = 20f

            background = createSelectableThemedBackground(context, border, themeColor)

            setPadding(
                horizontalPaddingEditTextLeft,
                verticalPaddingEditText,
                horizontalPaddingEditTextRight,
                verticalPaddingEditText
            )

            if (isSelected) {
                isChecked = true
                setTextColor(radioButtonTextColor)
            }

            val layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            this.layoutParams = layoutParams
        }
    }

    private fun otherRadioButton(
        label: String,
        tag: Int,
        isSelected: Boolean,
        detailText: String
    ): Pair<RadioButton, EditText> {
        val radioButton = createRadioButton(label, tag, BackgroundDrawable.Border.None, isSelected)

        val editText = EditText(context).apply {
            this.tag = EDIT_TEXT_TAG
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            isSingleLine = false
            hint = detailText
            minLines = 3
            maxLines = 5

            val horizontalMarginEditText = context.px(
                context.resources.getDimension(R.dimen.text_field_horizontal_padding_left)
            ).toInt()
            val layoutParams = LinearLayout.LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            ).apply {
                marginStart = horizontalMarginEditText
                marginEnd = horizontalMarginEditText
            }
            this.layoutParams = layoutParams

            setBackgroundResource(R.drawable.edit_text_background)

            val paddingEditText = context.px(context.resources.getDimension(R.dimen.button_margin)).toInt()
            setPadding(paddingEditText, paddingEditText, paddingEditText, paddingEditText)

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    val text = editable.toString()
                    radioButton.isChecked = text.isNotEmpty()
                    onTextChangedListener(text)
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
        }

        radioButton.setOnCheckedChangeListener { _, isChecked ->
            val color: Int
            if (isChecked) {
                color = ContextCompat.getColor(context, R.color.black)
                if (!editText.hasFocus()) {
                    editText.requestFocus()
                    showKeyboard()
                }
            } else {
                color = ContextCompat.getColor(context, R.color.disabled_grey)
                editText.clearFocus()
                hideKeyboard(editText)
            }

            editText.setTextColor(color)
        }

        return radioButton to editText
    }

    private fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    //endregion

    init {
        this.id = R.id.singleChoicePart
        this.gravity = Gravity.CENTER

        this.setOnCheckedChangeListener { group, checkedId ->
            internalCheckedChangeListener(group, checkedId)
        }
    }
}
