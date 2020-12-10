/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.services.image_loader

import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File
import javax.inject.Inject

class ImageLoaderService @Inject constructor(): IImageLoaderService {

    override fun loadImageFromUrl(url: String, imageView: ImageView) {
        Glide
            .with(imageView.context)
            .load(url)
            .into(imageView)
    }

    override fun loadImageFromFile(file: File, imageView: ImageView) {
        Glide
            .with(imageView.context)
            .load(file)
            .into(imageView)
    }

}