package com.phamnhantucode.mycamera.core.presentation.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.ContextCompat.getString
import com.example.compose.MyCameraTheme
import com.phamnhantucode.mycamera.R

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = onConfirm
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmDeleteImagesDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    ConfirmDialog(
        title = getString(context, R.string.delete_images),
        message = getString(context, R.string.confirm_delete_images),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun ConfirmSaveOverwriteImage(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    ConfirmDialog(
        title = getString(context, R.string.save_image),
        message = getString(context, R.string.confirm_save_image),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@PreviewLightDark
@Composable
private fun ConfirmDeleteImagesDialogPreview() {
    MyCameraTheme {
        ConfirmDialog(
            title = "Delete Images",
            message = "Are you sure you want to delete these images?",
            onConfirm = {},
            onDismiss = {}
        )
    }
}