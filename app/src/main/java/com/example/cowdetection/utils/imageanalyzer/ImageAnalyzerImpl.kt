package com.example.cowdetection.utils.imageanalyzer

import android.graphics.Bitmap
import android.util.Log
import com.example.cowdetection.R
import com.example.cowdetection.utils.INPUT_IMAGE_HEIGHT
import com.example.cowdetection.utils.INPUT_IMAGE_WIDTH
import com.example.cowdetection.utils.filepath.FilePathProvider
import com.example.cowdetection.utils.prepostprocessor.model.AnalysisResult
import com.example.cowdetection.utils.prepostprocessor.PrePostProcessor
import com.example.cowdetection.utils.resource.ResourceProvider
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.torchvision.TensorImageUtils
import javax.inject.Inject

class ImageAnalyzerImpl @Inject constructor(
    private val filePathProvider: FilePathProvider,
    private val prePostProcessor: PrePostProcessor,
    private val resourceProvider: ResourceProvider
) : ImageAnalyzer {

    companion object {
        val NO_MEAN_RGB = floatArrayOf(0f, 0f, 0f)
        val NO_STD_RGB = floatArrayOf(1f, 1f, 1f)
    }

    private val module =
        LiteModuleLoader.load(
            filePathProvider.assetFilePath(
                resourceProvider.string(R.string.neural_network_filename)
            )
        )

    override suspend fun analyzeImage(
        bitmap: Bitmap,
        resultViewWidth: Int,
        resultViewHeight: Int
    ): AnalysisResult {
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

        val results = prePostProcessor.outputsToNMSPredictions(
            scores,
            imgScaleX,
            imgScaleY,
            ivScaleX,
            ivScaleY,
            0F,
            0F
        )
        return AnalysisResult(results)
    }
}