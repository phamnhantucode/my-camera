package com.phamnhantucode.mycamera.camera.presentation

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import com.phamnhantucode.mycamera.R

enum class FlashState {
    ON, OFF, AUTO
}

enum class CameraRatio {
    RATIO_3_4, RATIO_9_16, RATIO_FULL, RATIO_1_1
}

enum class CameraZoomRatio {
    RATIO_1X, RATIO_2X, RATIO_4X;

    fun getStringRes(context: Context): String {
        return when (this) {
            RATIO_1X -> context.getString(R.string.camera_zoom_ratio_1x)
            RATIO_2X -> context.getString(R.string.camera_zoom_ratio_2x)
            RATIO_4X -> context.getString(R.string.camera_zoom_ratio_4x)
        }
    }

    fun getFloatValue(): Float {
        return when (this) {
            RATIO_1X -> 1f
            RATIO_2X -> 2f
            RATIO_4X -> 4f
        }
    }
}

data class CameraState(
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val latestImageCapture: Bitmap? = null,
    val flashState: FlashState = FlashState.OFF,
    val cameraRatio: CameraRatio = CameraRatio.RATIO_3_4,
    val enableCameraNet: Boolean = false,
    val cameraZoomRatio: CameraZoomRatio = CameraZoomRatio.RATIO_1X,
)