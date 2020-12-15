/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.backend.domain

import com.google.gson.annotations.SerializedName

enum class ImageContentMode {
    @SerializedName("0")
    FILL,
    @SerializedName("1")
    ASPECT_FIT,
    @SerializedName("2")
    ASPECT_FILL
}