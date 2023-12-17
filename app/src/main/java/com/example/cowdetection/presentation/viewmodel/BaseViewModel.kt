package com.example.cowdetection.presentation.viewmodel

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cowdetection.domain.usecase.TakePhotoUseCase
import com.example.cowdetection.utils.filepath.FilePathProvider
import java.io.File
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val takePhotoUseCase: TakePhotoUseCase,
    private val filePathProvider: FilePathProvider
) : ViewModel() {

    private val _currentImage = MutableLiveData<Uri?>(null)
    val currentImage: LiveData<Uri?> = _currentImage

    private val _imageCapture = MutableLiveData<ImageCapture>()
    val imageCapture: LiveData<ImageCapture> = _imageCapture

    fun saveImageUri(imageUri: Uri?) {
        _currentImage.value = imageUri
    }

    fun saveImageCapture(imageCapture: ImageCapture) {
        _imageCapture.value = imageCapture
    }

    fun getOutputDirectory(): File {
        return filePathProvider.ioDirectoryPath()
    }
}