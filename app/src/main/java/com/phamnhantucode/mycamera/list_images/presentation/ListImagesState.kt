package com.phamnhantucode.mycamera.list_images.presentation

import androidx.compose.ui.graphics.ImageBitmap


data class ListImagesState(
    val images: List<ImageBitmap> = emptyList(),
    val selectedImagesIndex: List<Int> = emptyList(),
    val isLoading: Boolean = false,
)