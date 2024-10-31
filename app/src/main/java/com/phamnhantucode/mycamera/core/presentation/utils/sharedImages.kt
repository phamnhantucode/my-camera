package com.phamnhantucode.mycamera.core.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.phamnhantucode.mycamera.R

fun sharedImages(context: Context, imageUris: List<Uri>) {
    val sharedIntent = Intent().apply {
        if (imageUris.size > 1) {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
        } else {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUris.first())
        }
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(
            sharedIntent,
            context.getString(R.string.share)
        )
    )
}