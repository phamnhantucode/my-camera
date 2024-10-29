package com.phamnhantucode.mycamera.camera.presentation

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CameraState())

    fun onAction(action: CameraAction) {
        when (action) {
            is CameraAction.TakenPicture -> {
                viewModelScope.launch {
                    storageKeeper.saveImage(
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
        }
    }
}