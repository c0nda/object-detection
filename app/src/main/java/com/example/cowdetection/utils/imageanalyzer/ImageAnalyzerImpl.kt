package com.example.cowdetection.utils.imageanalyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import com.example.cowdetection.utils.INPUT_IMAGE_HEIGHT
import com.example.cowdetection.utils.INPUT_IMAGE_WIDTH
import com.example.cowdetection.utils.filepath.FilePathProvider
import com.example.cowdetection.utils.prepostprocessor.AnalysisResult
import com.example.cowdetection.utils.prepostprocessor.PrePostProcessor
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.torchvision.TensorImageUtils
import javax.inject.Inject

class ImageAnalyzerImpl @Inject constructor(
    private val filePathProvider: FilePathProvider,
    private val prePostProcessor: PrePostProcessor,
    private val context: Context
) : ImageAnalyzer {

    companion object {
        val NO_MEAN_RGB = floatArrayOf(0f, 0f, 0f)
        val NO_STD_RGB = floatArrayOf(1f, 1f, 1f)
    }

    private val module =
        LiteModuleLoader.load(filePathProvider.assetFilePath("weights.torchscript.ptl"))

    override suspend fun analyzeImage(uri: Uri, resultViewWidth: Int, resultViewHeight: Int): AnalysisResult {
        var bitmap = MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            uri
        )
        val matrix = Matrix()
        matrix.postRotate(90F)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            INPUT_IMAGE_WIDTH,
            INPUT_IMAGE_HEIGHT,
            true
        )
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            NO_MEAN_RGB,
            NO_STD_RGB
        )
        val outputTensor = module?.forward(IValue.from(inputTensor))?.toTuple()?.get(0)?.toTensor()
        val scores = outputTensor?.dataAsFloatArray!!

        val imgScaleX = bitmap.width.toFloat() / INPUT_IMAGE_WIDTH
        val imgScaleY = bitmap.height.toFloat() / INPUT_IMAGE_HEIGHT
        val ivScaleX = resultViewWidth.toFloat() / bitmap.width
        val ivScaleY = resultViewHeight.toFloat() / bitmap.height

        val results = prePostProcessor.outputsToNMSPredictions(scores, imgScaleX, imgScaleY, ivScaleX, ivScaleY, 0F, 0F)
        return AnalysisResult(results)
    }
}