package com.example.cowdetection.utils.imageanalyzer

import android.graphics.Bitmap
import com.example.cowdetection.utils.filepath.FilePathProvider
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.torchvision.TensorImageUtils
import javax.inject.Inject

class ImageAnalyzerImpl @Inject constructor(
    private val filePathProvider: FilePathProvider
) : ImageAnalyzer {

    private val module =
        LiteModuleLoader.load(filePathProvider.assetFilePath("weights.torchscript.ptl"))

    override suspend fun analyzeImage(bitmap: Bitmap): Float {
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB,
            MemoryFormat.CHANNELS_LAST
        )
        val outputTensor = module?.forward(IValue.from(inputTensor))?.toTuple()?.get(0)?.toTensor()
        val scores = outputTensor?.dataAsFloatArray
        var maxScore = -Float.MAX_VALUE
        var maxScoreIndex = -1

        if (scores != null) {
            for (i in scores.indices) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i]
                    maxScoreIndex = i
                }
            }
        }
        return maxScore
    }

}