/*
 * Copyright (c) 2020 FutureWorkshops. All rights reserved.
 */

package com.quickbirdstudios.surveykit.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createFile(baseFolder: File, extension: String): File {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
    return File(
        baseFolder,
        simpleDateFormat.format(System.currentTimeMillis()) + extension
    )
}

fun File.toBitmap(): Bitmap = BitmapFactory.decodeFile(absolutePath)

fun Bitmap.flip(): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(90f)
    matrix.preScale(-1f, 1f)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

fun Bitmap.rotate(): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(90f)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}