package com.phamnhantucode.mycamera.edit_image.presentation

import android.graphics.Rect

sealed interface CropRotateAction {
    data class Rotate(val degree: Int) : CropRotateAction
    data object RotateLeft90Degrees : CropRotateAction
    data object Flip : CropRotateAction
    data class CropRectChange(val rect: Rect) : CropRotateAction
}