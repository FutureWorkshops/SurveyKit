package com.quickbirdstudios.surveykit.backend.views.main_parts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.quickbirdstudios.surveykit.R
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.backend.helpers.extensions.verticalMargin

class Content @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleRes), StyleablePart {

    //region Member

    private val root: View = View.inflate(context, R.layout.layout_content, this)
    private val container: ViewGroup = root.findViewById(R.id.content_container)
    private val footerContainer: ViewGroup = root.findViewById(R.id.footer_container)
    private val footer: Footer = Footer(context).apply { id = R.id.questionFooter }

    //endregion

    //region Public API

    fun <T : View> add(view: T): T {
        container.addView(view)
        return view
    }

    fun clear() {
        container.removeAllViews()
    }

    fun hideFooterContainer() {
        footerContainer.visibility = View.GONE
    }

    fun changeContainerVerticalMargins(marginInDp: Int) {
        container.verticalMargin(marginInDp)
    }

    fun makeContainerMatchParent() {
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    //endregion

    //region Overrides

    override fun style(surveyTheme: SurveyTheme) {
        (0 until container.childCount).forEach {
            (container.getChildAt(it) as? StyleablePart)?.style(surveyTheme)
        }
        footer.style(surveyTheme)
    }

    //endregion

    init {
        this.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        ).apply {
            this.weight = 1f
        }
        footerContainer.addView(footer)
    }
}
