package com.phamnhantucode.mycamera.edit_image.presentation

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class EditImageViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {
    private val _state = MutableStateFlow(EditImageState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditImageState())

    private val _cropRotateState = MutableStateFlow(CropRotateState())
    val cropRotateState = _cropRotateState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CropRotateState())

    private val _events = Channel<EditImageEvent>()
    val events = _events.receiveAsFlow()

    private lateinit var fileImage: File

    fun onImageAction(action: EditImageAction) {
        viewModelScope.launch {
            when (action) {
                is EditImageAction.LoadImage -> {
                    _state.update {
                        EditImageState(
                            image = storageKeeper.getImage(action.path)?.let {
                                fileImage = it
                                BitmapFactory.decodeFile(it.absolutePath).asImageBitmap()
                            }
                        )
                    }
                }

                EditImageAction.EditImage -> {
                    _state.update {
                        it.copy(isEditing = true)
                    }
                }

                EditImageAction.DeleteImage -> {
                    storageKeeper.deleteImage(fileImage.absolutePath)
                    _state.update {
                        it.copy(image = null)
                    }
                }

                EditImageAction.ClickedImage -> {
                    Log.d(
                        "EditImageViewModel",
                        "Clicked image: ${_state.value.isShowingActionButtons}"
                    )
                    _state.update {
                        it.copy(isShowingActionButtons = !it.isShowingActionButtons)
                    }
                }

                EditImageAction.BackToOriginal -> {
                    _state.update {
                        EditImageState(
                            image = fileImage.let {
                                BitmapFactory.decodeFile(it.absolutePath).asImageBitmap()
                            }
                        )
                    }
                    _cropRotateState.update {
                        CropRotateState()
                    }
                }

                EditImageAction.SaveWithNew -> {
                    val uri = storageKeeper.generateNewImageUri()
                    _events.send(EditImageEvent.CropImage(uri))
                }

                EditImageAction.SaveWithReplace -> {
                    _events.send(EditImageEvent.CropImage(Uri.fromFile(fileImage)))
                }
            }
        }
    }

    fun onCropRotateAction(action: CropRotateAction) {
        viewModelScope.launch {
            when (action) {
                is CropRotateAction.Rotate -> {
                }

                CropRotateAction.Flip -> {
                    _cropRotateState.update {
                        it.copy(flipX = !it.flipX)
                    }
                }

                CropRotateAction.RotateLeft90Degrees -> {
                    _cropRotateState.update {
                        it.copy(rotateZDegree = it.rotateZDegree - 90)
                    }
                }
            }
        }
    }

    fun getImageUri(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            fileImage
        )
    }
}