package com.phamnhantucode.mycamera.list_images.presentation

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
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
import java.io.File

class ListImagesViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {
    private val _state = MutableStateFlow(ListImagesState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListImagesState())

    private val _events = Channel<ListImagesEvent>()
    val events = _events
        .receiveAsFlow()

    private var fileImages = emptyList<File>()

    fun getImages() {
        viewModelScope.launch {
            fileImages = storageKeeper.getImages()
            _state.update {
                it.copy(images = fileImages.map { file ->
                    BitmapFactory.decodeFile(file.absolutePath).asImageBitmap()
                })
            }
        }
    }

    fun onAction(action: ListImagesAction) {
        viewModelScope.launch {
            when (action) {
                is ListImagesAction.SelectImage -> {
                    _state.update {
                        it.copy(
                            selectedImagesIndex = it.selectedImagesIndex.toMutableList().apply {
                                if (contains(action.index)) {
                                    remove(action.index)
                                } else {
                                    add(action.index)
                                }
                            }
                        )
                    }
                }

                ListImagesAction.ClearSelectedImages -> {
                    _state.update {
                        it.copy(selectedImagesIndex = emptyList())
                    }
                }

                is ListImagesAction.DeleteSelectedImages -> {
                    try {
                        storageKeeper.deleteImagesByFiles(
                            fileImages.filterIndexed { index, _ ->
                                state.value.selectedImagesIndex.contains(index)
                            }
                        )
                    } catch (e: Exception) {
                        TODO()
                    }
                    getImages()
                    _state.update {
                        it.copy(
                            selectedImagesIndex = emptyList()
                        )
                    }
                }

                is ListImagesAction.OpenImage -> {
                    _events.send(ListImagesEvent.NavigateToEditImage(fileImages[action.index].absolutePath))
                }

                ListImagesAction.BackNavigate -> {
                    _events.send(ListImagesEvent.BackNavigate)
                }
            }
        }
    }

    fun getImageUri(context: Context, index: Int): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            fileImages[index]
        )
    }
}