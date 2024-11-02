package com.phamnhantucode.mycamera.camera.presentation

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state
        .onStart {
            retrieveNewestImage()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CameraState())

    private val _events = Channel<CameraUiEvent>()
    val events = _events
        .receiveAsFlow()

    fun onAction(action: CameraAction) {
        viewModelScope.launch {
            when (action) {
                is CameraAction.TakenPicture -> {
                        storageKeeper.saveNewImage(
                            action.path,
                            action.pictureTaken
                        ) {
                            _state.update {
                                it.copy(
                                    latestImageCapture = action.pictureTaken
                                )
                            }
                        }
                }
                is CameraAction.SwitchCamera -> {
                    val cameraSelector = when (state.value.cameraSelector) {
                        CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
                        else -> {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    }
                    _state.update {
                        it.copy(cameraSelector = cameraSelector)
                    }
                }
                is CameraAction.NavigateToListImages -> {
                    _events.send(CameraUiEvent.NavigateToListImages)
                }
                is CameraAction.ChangeFlashState -> {
                    _state.update {
                        it.copy(flashState = action.flashState)
                    }
                }
                is CameraAction.EnableCameraNet -> {
                    _state.update {
                        it.copy(enableCameraNet = action.enable)
                    }
                }
            }
        }
    }

    private fun retrieveNewestImage() {
        viewModelScope.launch {
            storageKeeper.getNewestImage()?.let {bitmap ->
                _state.update {
                    it.copy(latestImageCapture = bitmap)
                }
            }
        }
    }
}