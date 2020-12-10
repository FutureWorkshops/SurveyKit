/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services

import com.quickbirdstudios.surveykit.services.image_loader.IImageLoaderService
import com.quickbirdstudios.surveykit.services.localization.LocalizationService
import com.quickbirdstudios.surveykit.services.network.INetworkService

class MobileWorkflowServices(
    val imageLoaderService: IImageLoaderService,
    val localizationService: LocalizationService,
    val networkService: INetworkService
)