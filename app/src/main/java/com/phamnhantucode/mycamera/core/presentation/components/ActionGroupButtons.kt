package com.phamnhantucode.mycamera.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.phamnhantucode.mycamera.R

@Composable
fun ActionGroupButtons(
    modifier: Modifier = Modifier,
    onShared: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (onEdit != null) {
            IconButton(
                modifier = Modifier.size(56.dp),
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = context.getString(R.string.edit),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (onShared != null) {
            IconButton(
                modifier = Modifier.size(56.dp),
                onClick = onShared
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = context.getString(R.string.share),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (onDelete != null) {
            IconButton(
                modifier = Modifier.size(56.dp),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = context.getString(R.string.delete),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}