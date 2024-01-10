package com.example.cowdetection.utils.prepostprocessor

import android.graphics.Rect

data class Result(
    val classIndex: Int,
    val score: Float,
    val rect: Rect
)