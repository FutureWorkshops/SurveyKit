/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services

import com.quickbirdstudios.surveykit.backend.helpers.LocalizationService
import com.quickbirdstudios.surveykit.services.image_loader.ImageLoaderService

class MobileWorkflowServices(
    val imageLoaderService: ImageLoaderService,
    val localizationService: LocalizationService
)