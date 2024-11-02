package com.phamnhantucode.mycamera.camera.presentation

import android.graphics.Bitmap

sealed class CameraAction {
    data class TakenPicture(val pictureTaken: Bitmap, val path: String) : CameraAction()
    data object SwitchCamera : CameraAction()
    data object NavigateToListImages : CameraAction()
    data class ChangeFlashState(val flashState: FlashState) : CameraAction()
    data class EnableCameraNet(val enable: Boolean) : CameraAction()

}