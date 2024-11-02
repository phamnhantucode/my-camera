package com.phamnhantucode.mycamera.edit_image.presentation

import android.graphics.Rect
import android.net.Uri

sealed interface EditImageEvent {
    data object BackNavigate : EditImageEvent
}