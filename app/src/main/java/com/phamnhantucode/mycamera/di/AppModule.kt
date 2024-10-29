package com.phamnhantucode.mycamera.di

import com.phamnhantucode.mycamera.camera.presentation.CameraViewModel
import com.phamnhantucode.mycamera.core.helper.StorageKeeper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

val appModule = module {
    single { StorageKeeper(get()) }
    viewModel { CameraViewModel(get()) }
}