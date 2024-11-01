package com.phamnhantucode.mycamera.edit_image.presentation

import android.graphics.Rect

data class CropRotateState(
    val rotateZDegree: Int = 0,
    val flipX: Boolean = false,
    val cropRect: Rect? = null
)
