package com.example.cowdetection.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.camera.core.ImageCapture
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cowdetection.utils.INPUT_IMAGE_HEIGHT
import com.example.cowdetection.utils.INPUT_IMAGE_WIDTH
import com.example.cowdetection.utils.contentresolver.ContentResolverProvider
import com.example.cowdetection.utils.filepath.FilePathProvider
import com.example.cowdetection.utils.imageanalyzer.ImageAnalyzer
import com.example.cowdetection.utils.prepostprocessor.model.AnalysisResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val filePathProvider: FilePathProvider,
    private val imageAnalyzer: ImageAnalyzer,
    private val contentResolverProvider: ContentResolverProvider
) : ViewModel() {

    private val _currentImage = MutableLiveData<Uri?>(null)
    val currentImage: LiveData<Uri?> = _currentImage

    private val _imageCapture = MutableLiveData<ImageCapture>()
    val imageCapture: LiveData<ImageCapture> = _imageCapture

    init {
        _imageCapture.value = ImageCapture.Builder()
            .setTargetResolution(Size(INPUT_IMAGE_WIDTH, INPUT_IMAGE_HEIGHT))
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

    suspend fun analyzeImage(
        bitmap: Bitmap,
        resultViewWidth: Int,
        resultViewHeight: Int
    ): AnalysisResult = coroutineScope {
        val analysis = async { imageAnalyzer.analyzeImage(bitmap, resultViewWidth, resultViewHeight) }
        return@coroutineScope analysis.await()
    }

    fun createBitmap(uri: Uri): Bitmap {
        var bitmap = MediaStore.Images.Media.getBitmap(
            contentResolverProvider.contentResolver(),
            uri
        )
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            INPUT_IMAGE_WIDTH,
            INPUT_IMAGE_HEIGHT,
            true
        )
        val matrix = Matrix()
        matrix.postRotate(90F)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return bitmap
    }
}