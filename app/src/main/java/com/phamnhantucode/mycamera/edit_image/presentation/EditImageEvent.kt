package com.phamnhantucode.mycamera.edit_image.presentation

import android.graphics.Rect
import android.net.Uri

sealed interface EditImageEvent {
    data class CropImage(val uri: Uri, val rect: Rect?) : EditImageEvent
}