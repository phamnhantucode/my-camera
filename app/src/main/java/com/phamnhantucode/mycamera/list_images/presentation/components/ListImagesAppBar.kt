@file:OptIn(ExperimentalMaterial3Api::class)

package com.phamnhantucode.mycamera.list_images.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesAction
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesState
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListImagesAppBar(
    state: ListImagesState,
    viewModel: ListImagesViewModel = koinViewModel<ListImagesViewModel>()
) {
    val context = LocalContext.current
    val titleString = if (state.selectedImagesIndex.isNotEmpty()) {
        context.getString(R.string.selected_images, state.selectedImagesIndex.size)
    } else {
        context.getString(R.string.list_images)
    }
    TopAppBar(
        title = { Text(titleString) },
        navigationIcon = {
            if (state.selectedImagesIndex.isEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.onAction(ListImagesAction.BackNavigate)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = context.getString(R.string.back)
                    )
                }
            }
        },
        actions = {
            if (state.selectedImagesIndex.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.onAction(ListImagesAction.ClearSelectedImages)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = context.getString(R.string.delete)
                    )
                }
            }
        }
    )
}