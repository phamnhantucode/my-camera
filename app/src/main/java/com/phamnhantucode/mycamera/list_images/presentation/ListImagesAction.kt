package com.phamnhantucode.mycamera.list_images.presentation

sealed interface ListImagesAction {
    data class SelectImage(val index: Int) : ListImagesAction
    data object ClearSelectedImages : ListImagesAction
    data object DeleteSelectedImages : ListImagesAction
    data class OpenImage(val index: Int) : ListImagesAction
    data object BackNavigate : ListImagesAction
}