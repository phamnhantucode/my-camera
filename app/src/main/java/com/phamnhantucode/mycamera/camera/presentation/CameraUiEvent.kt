package com.phamnhantucode.mycamera.camera.presentation

sealed interface CameraUiEvent {
    data object NavigateToListImages : CameraUiEvent
}