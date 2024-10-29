package com.phamnhantucode.mycamera.core.helper

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class StorageKeeper(
    val context: Context
) {
    suspend fun saveImage(path: String, bitmap: Bitmap, onSaved: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            var fileName = ZonedDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            )
            fileName += UUID.randomUUID().toString()
            fileName += ".jpeg"
            val file = File(path, fileName)
            val result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
            if (result) {
                withContext(Dispatchers.Main) {
                    onSaved(file.absolutePath)
                }
            }
        }
    }
}