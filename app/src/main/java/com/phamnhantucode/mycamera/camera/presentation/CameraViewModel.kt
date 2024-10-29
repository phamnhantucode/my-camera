package com.phamnhantucode.mycamera.camera.presentation

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class CameraViewModel() : ViewModel() {

    val _state = MutableStateFlow(CameraState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CameraState())

    fun onAction(action: CameraAction) {
        when (action) {
            is CameraAction.TakenPicture -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val path = action.path
                        print(path)
                        var fileName = ZonedDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                        )
                        fileName += UUID.randomUUID().toString()
                        fileName += ".jpeg"
                        val file = File(path, fileName)
                        val result = action.pictureTaken.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
                        if (result) {
                            _state.update {
                                it.copy(latestImageCapture = action.pictureTaken)
                            }
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