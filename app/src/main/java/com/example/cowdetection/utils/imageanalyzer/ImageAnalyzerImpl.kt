package com.example.cowdetection.utils.imageanalyzer

import android.graphics.Bitmap
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
    filePathProvider: FilePathProvider,
    private val prePostProcessor: PrePostProcessor,
    resourceProvider: ResourceProvider
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
        resultViewHeight: Int,
        sourceBitmapWidth: Int,
        sourceBitmapHeight: Int
    ): AnalysisResult {
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            NO_MEAN_RGB,
            NO_STD_RGB
        )
        val outputTensor = module?.forward(IValue.from(inputTensor))?.toTuple()?.get(0)?.toTensor()
        val scores = outputTensor?.dataAsFloatArray!!

        val imgScaleX = sourceBitmapWidth.toFloat() / INPUT_IMAGE_WIDTH
        val imgScaleY = sourceBitmapHeight.toFloat() / INPUT_IMAGE_HEIGHT
        val ivScaleX = resultViewWidth.toFloat() / sourceBitmapWidth
        val ivScaleY = resultViewHeight.toFloat() / sourceBitmapHeight
        val startX = (resultViewWidth - ivScaleX * sourceBitmapWidth) / 2
        val startY = (resultViewHeight - ivScaleY * sourceBitmapHeight) / 2

        val results = prePostProcessor.outputsToNMSPredictions(
            scores,
            imgScaleX,
            imgScaleY,
            ivScaleX,
            ivScaleY,
            startX,
            startY
        )
        return AnalysisResult(results)
    }
}