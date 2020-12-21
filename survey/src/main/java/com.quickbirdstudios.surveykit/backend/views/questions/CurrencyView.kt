package com.quickbirdstudios.surveykit.backend.views.questions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.question_parts.CurrencyPart
import com.quickbirdstudios.surveykit.backend.views.step.QuestionView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.question_results.CurrencyQuestionResult
import kotlinx.android.synthetic.main.currency_step.*
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*


internal class CurrencyView(
    id: StepIdentifier,
    isOptional: Boolean,
    title: String,
    text: String,
    nextButtonText: String,
    private val currencyAnswerFormat: AnswerFormat.CurrencyAnswerFormat,
    private val preselected: CurrencyQuestionResult? = null
) : QuestionView(id, isOptional, title, text, nextButtonText) {

    private lateinit var currencyPart: CurrencyPart

    override fun createResults(): QuestionResult {
        return CurrencyQuestionResult(
            stringIdentifier = id.id,
            id = id,
            startDate = startDate,
            endDate = Date(),
            currencyCode = currencyAnswerFormat.currencyCode,
            value = getValueFromEditText().toBigDecimal().parse()
        )
    }

    override fun isValidInput(): Boolean = getValueFromEditText().isNotEmpty()

    private fun getValueFromEditText() = valueEt.text.toString().getJustNumbers()

    override fun setupViews() {
        super.setupViews()
        context?.let {
            currencyPart = CurrencyPart(it)
            content.add(currencyPart)

            val moneyTextWatcher = MoneyTextWatcher(valueEt, currencyAnswerFormat.currencyCode) {
                footer.canContinue = isValidInput()
            }

            preselected?.value?.let { moneyTextWatcher.setValue(it, true) }
            valueEt.addTextChangedListener(moneyTextWatcher)
        }
    }

    private class MoneyTextWatcher(
        editText: EditText?,
        private val currencyCode: String,
        private val onAfterTextChangedFinished: () -> Unit
    ) : TextWatcher {

        private val editTextWeakReference: WeakReference<EditText> = WeakReference<EditText>(editText)

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable) {
            val editText: EditText = editTextWeakReference.get() ?: return
            val s = editable.toString()
            if (s.isEmpty()) return
            editText.removeTextChangedListener(this)

            val cleanString: String = s.getJustNumbers()
            setValue(cleanString.toBigDecimal(), false)
            editText.addTextChangedListener(this)

            onAfterTextChangedFinished()
        }

        private fun getCurrencySymbol(currencyCode: String?): String? = try {
            val currency = Currency.getInstance(currencyCode)
            currency.symbol
        } catch (e: Exception) {
            currencyCode
        }

        fun setValue(value: BigDecimal, isParsed: Boolean) {
            val editText: EditText = editTextWeakReference.get() ?: return
            val parsed: BigDecimal = if (isParsed) value else value.parse()
            val formatted: String = NumberFormat.getCurrencyInstance().format(parsed)

            val currencySymbol = getCurrencySymbol(currencyCode) ?: ""
            val localeCurrencySymbol = NumberFormat.getCurrencyInstance().currency?.symbol ?: ""
            val finalString: String = formatted.replace(localeCurrencySymbol, "$currencySymbol ")

            editText.setText(finalString)
            editText.setSelection(finalString.length)
        }

    }

}

private fun String.getJustNumbers(): String = this.replace("[^0-9]".toRegex(), "")

private fun BigDecimal.parse(): BigDecimal = this.setScale(2, BigDecimal.ROUND_FLOOR)
    .divide(BigDecimal(100), BigDecimal.ROUND_FLOOR)