package com.example.cowdetection.utils.imageanalyzer

import android.net.Uri
import com.example.cowdetection.utils.prepostprocessor.AnalysisResult

interface ImageAnalyzer {

    suspend fun analyzeImage(uri: Uri, resultViewWidth: Int, resultViewHeight: Int): AnalysisResult

}