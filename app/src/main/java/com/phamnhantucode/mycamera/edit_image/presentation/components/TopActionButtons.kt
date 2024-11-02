package com.phamnhantucode.mycamera.edit_image.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import com.phamnhantucode.mycamera.R

@Composable
fun TopActionButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSaveWithOverwrite: () -> Unit = {},
    onSaveWithNew: () -> Unit = {}
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onBack) {
            Text(getString(context, R.string.back))
        }
        TextButton(onClick = onSaveWithNew) {
            Text(getString(context, R.string.save_a_copy))
        }
        TextButton(onClick = onSaveWithOverwrite) {
            Text(getString(context, R.string.save_with_replace))
        }
    }
}