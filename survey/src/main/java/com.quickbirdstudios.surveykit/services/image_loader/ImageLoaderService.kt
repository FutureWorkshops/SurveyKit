/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services.image_loader

import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

class ImageLoaderService {

    fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide
            .with(imageView.context)
            .load(url)
            .into(imageView)
    }

    fun loadImageFromFile(file: File, imageView: ImageView) {
        Glide
            .with(imageView.context)
            .load(file)
            .into(imageView)
    }

}