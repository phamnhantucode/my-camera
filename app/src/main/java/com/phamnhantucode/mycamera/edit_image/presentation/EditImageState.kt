package com.phamnhantucode.mycamera.edit_image.presentation

import androidx.compose.ui.graphics.ImageBitmap

data class EditImageState(
    val image: ImageBitmap,
    val isEditing: Boolean = false
)