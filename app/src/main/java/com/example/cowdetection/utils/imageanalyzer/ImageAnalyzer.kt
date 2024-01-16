package com.example.cowdetection.utils.imageanalyzer

import android.graphics.Bitmap
import com.example.cowdetection.utils.prepostprocessor.model.AnalysisResult

interface ImageAnalyzer {

    suspend fun analyzeImage(bitmap: Bitmap, resultViewWidth: Int, resultViewHeight: Int): AnalysisResult

}