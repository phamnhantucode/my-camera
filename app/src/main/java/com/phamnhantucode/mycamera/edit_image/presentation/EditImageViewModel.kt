package com.phamnhantucode.mycamera.edit_image.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import com.phamnhantucode.mycamera.core.helper.throttle
import com.phamnhantucode.mycamera.edit_image.presentation.adjustment.AdjustmentAction
import com.phamnhantucode.mycamera.edit_image.presentation.adjustment.AdjustmentState
import com.phamnhantucode.mycamera.edit_image.presentation.adjustment.AdjustmentType
import com.phamnhantucode.mycamera.edit_image.presentation.crop.CropRotateAction
import com.phamnhantucode.mycamera.edit_image.presentation.crop.CropRotateState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class EditImageViewModel(
    private val storageKeeper: StorageKeeper
) : ViewModel() {
    private val _state = MutableStateFlow(EditImageState())
    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EditImageState())

    private val _cropRotateState = MutableStateFlow(CropRotateState())
    val cropRotateState = _cropRotateState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CropRotateState())

    private val _adjustmentState = MutableStateFlow(AdjustmentState())
    val adjustmentState = _adjustmentState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdjustmentState())

    private val _events = Channel<EditImageEvent>()
    val events = _events.receiveAsFlow()

    private lateinit var fileImage: File
    private var actionInvokeWhenWaitingCropImageSuccess: (() -> Unit)? = null
    private val throttleAdjustment = throttle(200L) {
        val bitmap = _state.value.croppedImage
        if (bitmap != null) {
            _state.update {
                it.copy(
                    editedImage = adjustEditedImage(bitmap.asAndroidBitmap()).asImageBitmap()
                )
            }
        }
    }

    fun onImageAction(action: EditImageAction) {
        viewModelScope.launch {
            when (action) {
                is EditImageAction.LoadImage ->
                    actionLoadImage(action)
                is EditImageAction.LoadEditedImage -> _state.update {
                        it.copy(
                            croppedImage = action.uri.let { uri ->
                                fileImage = File(uri.path!!)
                                BitmapFactory.decodeFile(fileImage.absolutePath).asImageBitmap()
                            }
                        )
                    }
                EditImageAction.EditImage -> _state.update {
                        it.copy(isEditing = true)
                    }
                EditImageAction.DeleteImage -> {
                    storageKeeper.deleteImage(fileImage.absolutePath)
                    _state.update {
                        it.copy(originImage = null)
                    }
                }
                EditImageAction.ClickedImage -> _state.update {
                        it.copy(isShowingActionButtons = !it.isShowingActionButtons)
                    }
                EditImageAction.BackToOriginal -> {
                    _state.update {
                        EditImageState(
                            originImage = fileImage.let {
                                BitmapFactory.decodeFile(it.absolutePath).asImageBitmap()
                            }
                        )
                    }
                    _cropRotateState.update {
                        CropRotateState()
                    }
                }
                EditImageAction.SaveWithNew -> saveImage(isOverwrite = false)
                EditImageAction.SaveWithOverwrite -> saveImage(isOverwrite = true)
                is EditImageAction.ChangeEditType -> {
                    if (_state.value.editType == EditType.CROP && action.type != EditType.CROP) {
                        _events.send(
                            EditImageEvent.CropImage(
                                storageKeeper.generateCacheImageUri(),
                                _cropRotateState.value.cropRect
                            )
                        )
                    }
                    if (action.type == EditType.CROP) {
                        _state.update { state ->
                            state.copy(
                                originImageApplyEditState = adjustEditedImage(state.originImage!!.asAndroidBitmap()).asImageBitmap()
                            )
                        }
                    }
                    _state.update {
                        it.copy(editType = action.type)
                    }
                }
                EditImageAction.BackNavigate -> _events.send(EditImageEvent.BackNavigate)

            }
            throttleAdjustment()
        }
    }

    private suspend fun saveImage(isOverwrite: Boolean) {
        _state.update {
            it.copy(isEditing = false)
        }
        if (state.value.editedImage == null) {
            _events.send(
                EditImageEvent.CropImage(
                    storageKeeper.generateCacheImageUri(),
                    _cropRotateState.value.cropRect
                )
            )
            _state.update {
                it.copy(isWaitingCropImageSuccess = true)
            }
            actionInvokeWhenWaitingCropImageSuccess = {
                viewModelScope.launch {
                    if (isOverwrite) {
                        storageKeeper.saveOverwriteImage(
                            fileImage.path,
                            _state.value.editedImage!!.asAndroidBitmap()
                        ) {
                            onImageAction(EditImageAction.LoadImage(fileImage.absolutePath))
                        }
                    } else {
                        storageKeeper.saveNewImage(
                            fileImage.parent!!,
                            _state.value.editedImage!!.asAndroidBitmap()
                        ) {
                            onImageAction(EditImageAction.LoadImage(fileImage.absolutePath))
                        }
                    }
                }
            }
        } else {
            if (isOverwrite) {
                storageKeeper.saveOverwriteImage(
                    fileImage.path,
                    _state.value.editedImage!!.asAndroidBitmap()
                ) {
                    onImageAction(EditImageAction.LoadImage(fileImage.absolutePath))
                }
            } else {
                storageKeeper.saveNewImage(
                    fileImage.parent!!,
                    _state.value.editedImage!!.asAndroidBitmap()
                ) {
                    onImageAction(EditImageAction.LoadImage(fileImage.absolutePath))
                }
            }
        }
    }

    private suspend fun actionLoadImage(action: EditImageAction.LoadImage) {
        _state.update {
            if (it.isEditing) {
                val image = storageKeeper.getImage(action.path)?.let { file ->
                    fileImage = file
                    BitmapFactory.decodeFile(file.absolutePath).asImageBitmap()
                }
                it.copy(
                    croppedImage = image,
                    editedImage = image?.let { img -> adjustEditedImage(img.asAndroidBitmap()).asImageBitmap() },
                )
            } else {
                val image = storageKeeper.getImage(action.path)?.let { file ->
                    fileImage = file
                    BitmapFactory.decodeFile(file.absolutePath).asImageBitmap()
                }
                EditImageState(
                    originImage = image,
                    originImageApplyEditState = image?.let { img ->
                        adjustEditedImage(
                            img.asAndroidBitmap()
                        ).asImageBitmap()
                    },
                )
            }
        }
        if (!_state.value.isEditing) {
            _cropRotateState.update {
                CropRotateState()
            }
            _adjustmentState.update {
                AdjustmentState()
            }
        } else {
            if (state.value.isWaitingCropImageSuccess) {
                actionInvokeWhenWaitingCropImageSuccess?.invoke()
                actionInvokeWhenWaitingCropImageSuccess = null
            }
        }
    }

    fun onCropRotateAction(action: CropRotateAction) {
        viewModelScope.launch {
            when (action) {
                is CropRotateAction.Rotate -> {
                }

                CropRotateAction.Flip -> {
                    _cropRotateState.update {
                        it.copy(flipX = !it.flipX)
                    }
                }

                CropRotateAction.RotateLeft90Degrees -> {
                    _cropRotateState.update {
                        it.copy(rotateZDegree = it.rotateZDegree - 90)
                    }
                }

                is CropRotateAction.CropRectChange -> {
                    _cropRotateState.update {
                        it.copy(cropRect = action.rect)
                    }
                }
            }
        }
    }

    fun onAdjustmentAction(action: AdjustmentAction) {
        viewModelScope.launch {
            when (action) {
                is AdjustmentAction.ChangeAdjustmentType -> {
                    if (_adjustmentState.value.currentAdjustmentType == action.type) {
                        _adjustmentState.update {
                            it.copy(
                                adjustments = it.adjustments.map { unit ->
                                    if (unit.type == action.type) {
                                        unit.copy(value = action.type.defaultValue)
                                    } else {
                                        unit
                                    }
                                }
                            )
                        }
                    } else {
                        _adjustmentState.update {
                            it.copy(currentAdjustmentType = action.type)
                        }
                    }
                }

                is AdjustmentAction.ChangeAdjustmentValue -> {
                    _adjustmentState.update { state ->
                        state.copy(adjustments = state.adjustments.map { unit ->
                            if (unit.type == state.currentAdjustmentType) {
                                unit.copy(value = action.value)
                            } else {
                                unit
                            }
                        })
                    }
                }

                AdjustmentAction.ResetAdjustment -> {
                    _adjustmentState.update {
                        it.copy(adjustments = it.adjustments.map { adjustmentUnit ->
                            adjustmentUnit.copy(value = adjustmentUnit.type.defaultValue)
                        })
                    }
                }
            }
            throttleAdjustment()
        }
    }

    private fun adjustEditedImage(originBitmap: Bitmap): Bitmap {
        val brightness =
            AdjustmentType.Brightness.toFloatValue(_adjustmentState.value.adjustments.first { it.type == AdjustmentType.Brightness }.value)
        val contrast =
            AdjustmentType.Contrast.toFloatValue(_adjustmentState.value.adjustments.first { it.type == AdjustmentType.Contrast }.value)
        val saturation =
            AdjustmentType.Saturation.toFloatValue(_adjustmentState.value.adjustments.first { it.type == AdjustmentType.Saturation }.value)
        val warmth =
            AdjustmentType.Warmth.toFloatValue(_adjustmentState.value.adjustments.first { it.type == AdjustmentType.Warmth }.value)
        val shadow =
            AdjustmentType.Shadow.toFloatValue(_adjustmentState.value.adjustments.first { it.type == AdjustmentType.Shadow }.value)
        val sharpness =
            AdjustmentType.Sharpness.toFloatValue(_adjustmentState.value.adjustments.first { it.type == AdjustmentType.Sharpness }.value)
        val brightnessColorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1f, 0f, 0f, 0f, brightness,  // Red
                    0f, 1f, 0f, 0f, brightness,  // Green
                    0f, 0f, 1f, 0f, brightness,  // Blue
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        val contrastColorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, (-0.5f * contrast + 0.5f) * 255f,  // Red
                    0f, contrast, 0f, 0f, (-0.5f * contrast + 0.5f) * 255f,  // Green
                    0f, 0f, contrast, 0f, (-0.5f * contrast + 0.5f) * 255f,  // Blue
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        val saturationColorMatrix = ColorMatrix().apply {
            val invSat = 1 - saturation
            val r = 0.213f * invSat
            val g = 0.715f * invSat
            val b = 0.072f * invSat
            val mat = floatArrayOf(
                r + saturation, g, b, 0f, 0f,
                r, g + saturation, b, 0f, 0f,
                r, g, b + saturation, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            set(mat)
        }
        val warmthColorMatrix = ColorMatrix().apply {
            set(
                floatArrayOf(
                    1f + warmth * 0.2f, 0f, 0f, 0f, 0f,  // Red
                    0f, 1f + warmth * 0.1f, 0f, 0f, 0f, // Green
                    0f, 0f, 1f, 0f, 0f,                        // Blue
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }

        val colorMatrix = ColorMatrix().apply {
            postConcat(brightnessColorMatrix)
            postConcat(contrastColorMatrix)
            postConcat(saturationColorMatrix)
            postConcat(warmthColorMatrix)
        }

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        var bitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        bitmap = adjustShadows(bitmap, (shadow * 100).toInt())
        bitmap = adjustSharpness(bitmap, sharpness)
        return bitmap

    }

    fun getImageUri(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            fileImage
        )
    }

    private fun adjustShadows(bitmap: Bitmap, shadowAdjustment: Int): Bitmap {
        if (shadowAdjustment == 0) {
            return bitmap
        }
        val width = bitmap.width
        val height = bitmap.height
        val adjustedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val shadowPixels = IntArray(width * height)

        for (y in (0 until height)) {
            for (x in (0 until width)) {
                val pixel = pixels[y * width + x]
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                if (r < 100 && g < 100 && b < 100) {
                    val newR = (r + shadowAdjustment).coerceIn(0, 255)
                    val newG = (g + shadowAdjustment).coerceIn(0, 255)
                    val newB = (b + shadowAdjustment).coerceIn(0, 255)

                    shadowPixels[y * width + x] = Color.rgb(newR, newG, newB)
                } else {
                    shadowPixels[y * width + x] = pixel
                }
            }
        }

        adjustedBitmap.setPixels(shadowPixels, 0, width, 0, 0, width, height)

        return adjustedBitmap
    }

    private fun adjustSharpness(bitmap: Bitmap, sharpnessFactor: Float): Bitmap {
        if (sharpnessFactor == 0f) {
            return bitmap
        }
        val kernel = arrayOf(
            floatArrayOf(0f, -1f * sharpnessFactor, 0f),
            floatArrayOf(-1f * sharpnessFactor, 1f + 4f * sharpnessFactor, -1f * sharpnessFactor),
            floatArrayOf(0f, -1f * sharpnessFactor, 0f)
        )

        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, bitmap.config)


        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val sharpenedPixels = IntArray(width * height)

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var r = 0f
                var g = 0f
                var b = 0f

                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val pixel = pixels[(y + ky) * width + (x + kx)]
                        val weight = kernel[ky + 1][kx + 1]
                        r += Color.red(pixel) * weight
                        g += Color.green(pixel) * weight
                        b += Color.blue(pixel) * weight
                    }
                }

                r = r.coerceIn(0f, 255f)
                g = g.coerceIn(0f, 255f)
                b = b.coerceIn(0f, 255f)

                sharpenedPixels[y * width + x] = Color.rgb(r.toInt(), g.toInt(), b.toInt())
            }
        }

        outputBitmap.setPixels(sharpenedPixels, 0, width, 0, 0, width, height)

        return outputBitmap
    }

}