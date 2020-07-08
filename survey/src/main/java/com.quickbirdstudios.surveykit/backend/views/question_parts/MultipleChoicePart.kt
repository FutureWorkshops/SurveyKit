package com.quickbirdstudios.surveykit.backend.views.question_parts

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
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
import com.quickbirdstudios.surveykit.backend.views.question_parts.helper.BackgroundDrawable.Border.None
import com.quickbirdstudios.surveykit.backend.views.question_parts.helper.createSelectableThemedBackground


private const val EDIT_TEXT_TAG = "EDIT_TEXT_TAG"

internal class MultipleChoicePart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), StyleablePart {

    //region Public API

    @ColorInt
    var themeColor: Int = ContextCompat.getColor(context, R.color.cyan_normal)
        set(color) {
            fields.forEachIndexed { index, checkBox ->
                val border = when (options[index]) {
                    is TextChoice.Normal -> if (index == 0) Both else Bottom
                    is TextChoice.Other -> None
                }
                checkBox.background = checkBox.createSelectableThemedBackground(context, border, color)
            }
            field = color
        }

    @ColorInt
    var checkBoxTextColor: Int = ContextCompat.getColor(context, R.color.cyan_text)
        set(color) {
            fields.forEach { it.setTextColor(defaultColor) }
            selectedCheckBoxes().forEach { it.setTextColor(color) }
            field = color
        }

    @ColorInt
    var defaultColor: Int = Color.BLACK

    var options: List<TextChoice> = emptyList()
        set(value) {
            update(value)
            field = value
        }

    var selected: List<TextChoice>
        get() = selectedChoices()
        set(list) {
            list.forEach { selected ->
                if (selected is TextChoice.Other) {
                    this.findViewWithTag<EditText>(EDIT_TEXT_TAG)?.setText(selected.result)
                } else {
                    val index = options.indexOfFirst { it.text == selected.text }
                    this.findViewWithTag<CheckBox>(index)?.isChecked = true
                }
            }
        }

    fun getEditTextSelection(): String? {
        return findViewWithTag<EditText>(EDIT_TEXT_TAG)?.text.toString()
    }

    fun isOneSelected(): Boolean = this.selectedChoices().isNotEmpty()

    var onCheckedChangeListener: (View, Boolean) -> Unit = { _, _ -> }

    var onTextChangedListener: (String) -> Unit = { }

    //endregion

    //region Overrides

    override fun style(surveyTheme: SurveyTheme) {
        themeColor = surveyTheme.themeColor
        checkBoxTextColor = surveyTheme.textColor
    }

    //endregion

    //region Private API

    private val fields: List<CheckBox>
        get() = (0 until this.childCount).mapNotNull { this.findViewWithTag(it) as? CheckBox }

    private fun update(list: List<TextChoice>) {
        val selectedChoices = selected
        this.removeAllViews()
        list.forEachIndexed { index, textChoice ->
            val isSelected = selectedChoices.contains(textChoice)
            val border = if (index == 0) Both else Bottom

            val item = when (textChoice) {
                is TextChoice.Normal -> createCheckBox(textChoice.text, index, border, isSelected)
                is TextChoice.Other -> otherCheckBox(textChoice.text, index, isSelected, textChoice.detailText)
            }

            this.addView(item)
        }
    }

    private fun selectedChoices(): List<TextChoice> {
        return fields
            .filter { it.isChecked }
            .map { options[it.tag as Int] }
            .filter {
                it is TextChoice.Normal || (it is TextChoice.Other && this.findViewWithTag<EditText>(EDIT_TEXT_TAG)?.text?.isNotEmpty() == true)
            }
    }

    private fun selectedCheckBoxes(): List<CheckBox> = fields.filter { it.isChecked }

    //endregion

    //region Internal listeners

    private val internalCheckedChangeListener: (View, Boolean) -> Unit = { checkBox, checked ->
        fields.forEach { it.setTextColor(defaultColor) }
        selectedCheckBoxes().forEach { it.setTextColor(checkBoxTextColor) }
        onCheckedChangeListener(checkBox, checked)
    }

    //endregion

    //region Checkbox Creation Helpers

    private fun createCheckBox(
        label: String,
        tag: Int,
        border: BackgroundDrawable.Border,
        isSelected: Boolean
    ): CheckBox {
        val verticalPaddingEditText = context.px(
            context.resources.getDimension(R.dimen.text_field_vertical_padding)
        ).toInt()
        val horizontalPaddingEditTextLeft = context.px(
            context.resources.getDimension(R.dimen.text_field_horizontal_padding_left)
        ).toInt()
        val horizontalPaddingEditTextRight = context.px(
            context.resources.getDimension(R.dimen.text_field_horizontal_padding_right)
        ).toInt()

        val checkBox = CheckBox(context).apply {
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

            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.layoutParams = layoutParams

            if (border != None) {
                addRipple()
                setOnClickListener { internalCheckedChangeListener(this, this.isChecked) }
            } else {
                isClickable = false
            }
        }

        if (isSelected) {
            checkBox.isChecked = true
            checkBox.setTextColor(checkBoxTextColor)
        }

        return checkBox
    }

    private fun otherCheckBox(
        label: String,
        tag: Int,
        isSelected: Boolean,
        detailText: String
    ): LinearLayout {
        val checkBox = createCheckBox(label, tag, None, isSelected)

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
            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
                    checkBox.isChecked = text.isNotEmpty()
                    onTextChangedListener(text)
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
        }

        val linearLayout = LinearLayout(context).apply {
            orientation = VERTICAL
            isClickable = true
            addRipple()
            setOnClickListener {
                if (editText.text.isNotEmpty()) {
                    checkBox.performClick()
                    internalCheckedChangeListener(this, checkBox.isChecked)
                } else if (!editText.hasFocus()) {
                    editText.requestFocus()
                    showKeyboard()
                }
            }

            val bottomPadding = context.px(context.resources.getDimension(R.dimen.text_field_vertical_padding)).toInt()
            setPadding(0, 0, 0, bottomPadding)
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
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

        linearLayout.addView(checkBox)
        linearLayout.addView(editText)
        return linearLayout
    }

    private fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun View.addRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = context.getDrawable(this.resourceId)
        }
    }

    //endregion

    init {
        this.let {
            id = R.id.multipleChoicePart
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            orientation = VERTICAL
        }
    }
}
