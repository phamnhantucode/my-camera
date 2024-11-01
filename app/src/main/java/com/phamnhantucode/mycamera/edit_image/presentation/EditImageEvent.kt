package com.phamnhantucode.mycamera.edit_image.presentation

import android.net.Uri

sealed interface EditImageEvent {
    data class CropImage(val uri: Uri) : EditImageEvent
}