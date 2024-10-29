package com.phamnhantucode.mycamera.camera.presentation

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector

data class CameraState(
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val latestImageCapture: Bitmap? = null,
)