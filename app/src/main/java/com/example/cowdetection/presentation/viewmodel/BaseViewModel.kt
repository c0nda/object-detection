package com.example.cowdetection.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cowdetection.domain.usecase.ChoosePhotoFromGalleryUseCase
import com.example.cowdetection.domain.usecase.TakePhotoUseCase

class BaseViewModel(
    private val choosePhotoFromGalleryUseCase: ChoosePhotoFromGalleryUseCase,
    private val takePhotoUseCase: TakePhotoUseCase
) : ViewModel() {

    private val _currentImage = MutableLiveData<Bitmap?>(null)
    val currentImage: LiveData<Bitmap?> = _currentImage


}