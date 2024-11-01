package com.phamnhantucode.mycamera.edit_image.presentation

import androidx.compose.ui.graphics.ImageBitmap

enum class EditType {
    CROP,
    ADJUSTMENT,
    FILTER
}

data class EditImageState(
    val originImage: ImageBitmap? = null,
    val croppedImage: ImageBitmap? = null,
    val editedImage: ImageBitmap? = null,
    val isEditing: Boolean = false,
    val isShowingActionButtons: Boolean = false,
    val editType: EditType = EditType.CROP,
)