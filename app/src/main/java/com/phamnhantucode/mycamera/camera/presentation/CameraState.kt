package com.phamnhantucode.mycamera.camera.presentation

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector

enum class FlashState {
    ON, OFF, AUTO
}

enum class CameraRatio {
    RATIO_3_4, RATIO_9_16, RATIO_FULL, RATIO_1_1
}

data class CameraState(
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val latestImageCapture: Bitmap? = null,
    val flashState: FlashState = FlashState.OFF,
    val cameraRatio: CameraRatio = CameraRatio.RATIO_3_4,
    val enableCameraNet: Boolean = false
)