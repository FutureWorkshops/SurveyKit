/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.views.main_parts

interface CanContinueListener {

    fun registerId(viewId: String, initialValue: Boolean)

    fun onContinueChange(viewId: String, state: Boolean)

}