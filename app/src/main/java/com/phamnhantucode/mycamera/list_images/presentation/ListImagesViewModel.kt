package com.phamnhantucode.mycamera.list_images.presentation

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

class ListImagesViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {
    private val _state = MutableStateFlow<ListImagesState>(ListImagesState())
    val state = _state
        .onStart { getImages() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListImagesState())

    private fun getImages() {
        viewModelScope.launch {
            _state.update {
                it.copy(images = storageKeeper.getImagesBitmap().map { bitmap -> bitmap.asImageBitmap() })
            }
        }
    }
}