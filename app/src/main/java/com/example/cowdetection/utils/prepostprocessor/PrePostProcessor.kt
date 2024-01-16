package com.example.cowdetection.utils.prepostprocessor

import com.example.cowdetection.utils.prepostprocessor.model.Result

interface PrePostProcessor {

    fun outputsToNMSPredictions(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        ivScaleX: Float,
        ivScaleY: Float,
        startX: Float,
        startY: Float
    ): ArrayList<Result>
}