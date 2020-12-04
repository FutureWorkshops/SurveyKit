/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.views.step

import android.view.Gravity

enum class ButtonOrientation(
    val gravity: Int
) {

    START(Gravity.START),
    CENTER(Gravity.CENTER_HORIZONTAL),
    END(Gravity.END)

}