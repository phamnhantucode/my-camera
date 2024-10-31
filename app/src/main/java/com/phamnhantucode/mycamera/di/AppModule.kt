package com.phamnhantucode.mycamera.di

import com.phamnhantucode.mycamera.camera.presentation.CameraViewModel
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import com.phamnhantucode.mycamera.edit_image.presentation.EditImageViewModel
import com.phamnhantucode.mycamera.list_images.presentation.ListImagesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

val appModule = module {
    single { StorageKeeper(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { ListImagesViewModel(get()) }
    viewModel { EditImageViewModel(get()) }
}