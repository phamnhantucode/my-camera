package com.phamnhantucode.mycamera.camera.presentation

import android.graphics.Bitmap

sealed class CameraAction {
    data class TakenPicture(val pictureTaken: Bitmap, val path: String) : CameraAction()
    data object SwitchCamera : CameraAction()
}