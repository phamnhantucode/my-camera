package com.phamnhantucode.mycamera.list_images.presentation

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class ListImagesViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {
    private val _state = MutableStateFlow(ListImagesState())
    val state = _state
        .onStart { getImages() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListImagesState())

    private var fileImages = emptyList<File>()

    private fun getImages() {
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
                is ListImagesAction.OpenImage -> TODO()
                is ListImagesAction.ShareSelectedImages -> TODO()
            }
        }
    }
}