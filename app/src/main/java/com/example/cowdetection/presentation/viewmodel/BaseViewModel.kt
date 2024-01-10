package com.example.cowdetection.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.camera.core.ImageCapture
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cowdetection.utils.filepath.FilePathProvider
import com.example.cowdetection.utils.imageanalyzer.ImageAnalyzer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val filePathProvider: FilePathProvider,
    private val imageAnalyzer: ImageAnalyzer
) : ViewModel() {

    private val _currentImage = MutableLiveData<Uri?>(null)
    val currentImage: LiveData<Uri?> = _currentImage

    private val _imageCapture = MutableLiveData<ImageCapture>()
    val imageCapture: LiveData<ImageCapture> = _imageCapture

    init {
        _imageCapture.value = ImageCapture.Builder()
            .setTargetResolution(Size(640, 640))
            .build()
    }

    fun saveImageUri(imageUri: Uri?) {
        _currentImage.value = imageUri
    }

    fun saveImageCapture(imageCapture: ImageCapture) {
        _imageCapture.value = imageCapture
    }

    fun getImageCapture(): ImageCapture? {
        return imageCapture.value
    }

    fun getOutputDirectory(): File {
        return filePathProvider.ioDirectoryPath()
    }

    suspend fun analyzeBitmap(bitmap: Bitmap): Float = coroutineScope {
        val analysis = async { imageAnalyzer.analyzeImage(bitmap) }
        return@coroutineScope analysis.await()
    }

}