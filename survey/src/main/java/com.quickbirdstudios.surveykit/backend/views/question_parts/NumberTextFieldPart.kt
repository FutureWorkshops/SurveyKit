package com.quickbirdstudios.surveykit.backend.views.question_parts

import android.content.Context
import android.text.InputType
import android.view.View
import androidx.annotation.StringRes
import com.quickbirdstudios.surveykit.R

internal class NumberTextFieldPart(context: Context) : TextFieldPart(context) {

    init {
        id = R.id.integerFieldPartField
        this.field.inputType = InputType.TYPE_CLASS_NUMBER
        this.textAlignment = View.TEXT_ALIGNMENT_CENTER
    }

    companion object {
        fun withHint(context: Context, @StringRes hint: Int) = NumberTextFieldPart(
            context
        ).apply {
            field.hint = context.getString(hint)
        }
    }
}
