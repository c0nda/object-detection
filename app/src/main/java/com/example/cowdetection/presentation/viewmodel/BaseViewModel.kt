package com.example.cowdetection.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cowdetection.domain.usecase.ChoosePhotoFromGalleryUseCase
import com.example.cowdetection.domain.usecase.TakePhotoUseCase
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val choosePhotoFromGalleryUseCase: ChoosePhotoFromGalleryUseCase,
    private val takePhotoUseCase: TakePhotoUseCase
) : ViewModel() {

    private val _currentImage = MutableLiveData<Uri?>(null)
    val currentImage: LiveData<Uri?> = _currentImage

    fun save(imageUri: Uri?) {
        _currentImage.value = imageUri
    }
}