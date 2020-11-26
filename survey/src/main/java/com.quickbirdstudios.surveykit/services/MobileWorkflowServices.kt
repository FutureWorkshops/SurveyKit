/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services

import android.content.Context
import com.quickbirdstudios.surveykit.backend.helpers.DictionaryHelper
import com.quickbirdstudios.surveykit.services.image_loader.ImageLoaderService

class MobileWorkflowServices(
    context: Context
) {

    val imageLoaderService = ImageLoaderService()

    val dictionaryHelper = DictionaryHelper(context)

}