package com.phamnhantucode.mycamera.core.navigation

import kotlinx.serialization.Serializable

sealed interface ScreenRoutes {
    @Serializable
    data object CameraScreen : ScreenRoutes
    @Serializable
    data object ListImagesScreen : ScreenRoutes
    @Serializable
    data class EditImageScreen(val path: String) : ScreenRoutes
}