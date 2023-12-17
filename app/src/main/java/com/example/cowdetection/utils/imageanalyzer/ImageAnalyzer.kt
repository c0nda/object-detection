package com.example.cowdetection.utils.imageanalyzer

import android.graphics.Bitmap

interface ImageAnalyzer {

    suspend fun analyzeImage(bitmap: Bitmap): Float

}