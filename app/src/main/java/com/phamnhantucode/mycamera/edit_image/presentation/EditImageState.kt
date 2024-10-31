package com.phamnhantucode.mycamera.edit_image.presentation

import androidx.compose.ui.graphics.ImageBitmap

data class EditImageState(
    val image: ImageBitmap? = null,
    val isEditing: Boolean = false,
    val isShowingActionButtons: Boolean = false,
)