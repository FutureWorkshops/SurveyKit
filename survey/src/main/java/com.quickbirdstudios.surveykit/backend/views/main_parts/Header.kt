package com.quickbirdstudios.surveykit.backend.views.main_parts

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.SurveyTheme

// TODO should take [Configuration] in constructor and remove public color setters and getters
class Header @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : AppBarLayout(context, attrs, defStyleRes),
    StyleablePart {

    //region Public API

    var themeColor = Color.RED
        set(value) {
            toolbar.menu.tintAllIcons(value)
            toolbar.overflowIcon?.setTint(value)
            toolbar.navigationIcon?.setTint(value)
            field = value
        }

    var canBack: Boolean
        get() = toolbar.visibility != View.VISIBLE
        set(value) {
            if (value) {
                toolbar.navigationIcon = ContextCompat.getDrawable(context, R.drawable.left_arrow)
                toolbar.navigationIcon?.setTint(themeColor)
            } else {
                toolbar.navigationIcon = null
            }
        }

    var onBack: () -> Unit = {}
    var onCancel: () -> Unit = {}

    //endregion

    //region Members

    private val root: View = View.inflate(context, R.layout.layout_header, this)

    private val toolbar: Toolbar = root.findViewById(R.id.toolbar)
    //endregion

    //region Overrides

    override fun style(surveyTheme: SurveyTheme) {
        themeColor = surveyTheme.themeColor
    }

    //endregion

    //region Private API

    // TODO this should probably not be done here
    fun hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
    //endregion
}
