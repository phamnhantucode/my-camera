package com.phamnhantucode.mycamera.core.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class StorageKeeper(
    private val context: Context
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

    suspend fun getImages(): List<File> {
       return withContext(Dispatchers.IO) {
            val dir = File(context.filesDir.absolutePath)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.mapNotNull { file ->
                    if (file.isFile && file.extension.equals("jpeg", true)) {
                        file
                    } else {
                        null
                    }
                } ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun deleteImagesByFiles(files: List<File>) {
        withContext(Dispatchers.IO) {
            files.forEach { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    suspend fun deleteImagesByPath(paths: List<String>) {
        withContext(Dispatchers.IO) {
            paths.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    suspend fun getImage(path: String): File? {
        return withContext(Dispatchers.IO) {
            val file = File(path)
            if (file.exists() && file.isFile) {
                file
            } else {
                null
            }
        }
    }

    suspend fun deleteImage(path: String) {
    }

    suspend fun generateNewImageUri(): Uri {
        return withContext(Dispatchers.IO) {
            var fileName = ZonedDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            )
            fileName += UUID.randomUUID().toString()
            val file = File(context.filesDir, "$fileName.jpeg")
            Uri.fromFile(file)
        }
    }
}