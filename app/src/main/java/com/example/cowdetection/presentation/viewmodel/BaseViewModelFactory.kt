package com.example.cowdetection.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cowdetection.domain.usecase.ChoosePhotoFromGalleryUseCase
import com.example.cowdetection.domain.usecase.TakePhotoUseCase


@Suppress("UNCHECKED_CAST")
class BaseViewModelFactory(
    private val choosePhotoFromGalleryUseCase: ChoosePhotoFromGalleryUseCase,
    private val takePhotoUseCase: TakePhotoUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseViewModel(
            choosePhotoFromGalleryUseCase = choosePhotoFromGalleryUseCase,
            takePhotoUseCase = takePhotoUseCase
        ) as T
    }
}