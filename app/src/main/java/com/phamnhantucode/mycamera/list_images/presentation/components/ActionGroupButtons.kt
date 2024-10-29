package com.phamnhantucode.mycamera.list_images.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesAction
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActionGroupButtons(
    viewModel: ListImagesViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            modifier = Modifier.size(56.dp),
            onClick = {
                viewModel.onAction(ListImagesAction.ShareSelectedImages)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = context.getString(R.string.share),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(
            modifier = Modifier.size(56.dp),
            onClick = {
                viewModel.onAction(ListImagesAction.DeleteSelectedImages)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = context.getString(R.string.delete),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}