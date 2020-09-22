package com.quickbirdstudios.surveykit.backend.helpers.extensions

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout

fun View.verticalMargin(margin: Int) {
    (this.layoutParams as LinearLayout.LayoutParams).setMargins(0, margin, 0, margin)
}

fun View.horizontalMargin(margin: Int) {
    (this.layoutParams as LinearLayout.LayoutParams).setMargins(margin, 0, margin, 0)
}

fun Int.toColorStateList(): ColorStateList {
    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(android.R.attr.state_pressed)
    )

    val colors = intArrayOf(
        this,
        Color.LTGRAY,
        this,
        this
    )

    return ColorStateList(states, colors)
}

fun Int.getTextColor(): Int {
    val red = Color.red(this) / 255
    val green = Color.green(this) / 255
    val blue = Color.blue(this) / 255

    val lum = 0.2126 * red + 0.7152 * green + 0.0722 * blue

    return if (lum < 0.75) Color.WHITE else Color.BLACK
}