/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services.image_loader

import android.widget.ImageView
import java.io.File

interface IImageLoaderService {

    fun loadImageFromUrl(url: String, imageView: ImageView)

    fun loadImageFromFile(file: File, imageView: ImageView)

}