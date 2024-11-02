package com.phamnhantucode.mycamera.list_images.presentation

sealed interface ListImagesEvent {
    data object BackNavigate : ListImagesEvent
    data class NavigateToEditImage(val path: String) : ListImagesEvent
}