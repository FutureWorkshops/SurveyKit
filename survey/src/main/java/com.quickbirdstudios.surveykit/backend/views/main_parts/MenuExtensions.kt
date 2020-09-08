/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.views.main_parts

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.drawable.DrawableCompat


fun Menu.tintAllIcons(color: Int) {
    for (i in 0 until size()) {
        val item: MenuItem = getItem(i)
        tintMenuItemIcon(color, item)
        tintMenuItemText(color, item)
    }
}

private fun tintMenuItemIcon(color: Int, item: MenuItem) {
    item.icon?.let { drawable ->
        val wrapped: Drawable = DrawableCompat.wrap(drawable)
        drawable.mutate()
        DrawableCompat.setTint(wrapped, color)
        item.icon = drawable
    }
}

private fun tintMenuItemText(color: Int, item: MenuItem) {
    val spanString = SpannableString(item.title.toString())
    spanString.setSpan(ForegroundColorSpan(color), 0, spanString.length, 0)
    item.title = spanString
}