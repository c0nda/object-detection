package com.example.cowdetection.utils.prepostprocessor.model

import android.graphics.Rect

data class Result(
    val classIndex: Int,
    val score: Float,
    val rect: Rect
)