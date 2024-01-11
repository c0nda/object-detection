package com.example.cowdetection.utils.prepostprocessor

import android.graphics.Rect
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class PrePostProcessorImpl @Inject constructor() : PrePostProcessor {

    companion object {
        const val outputRow = 25200
        const val outputColumn = 12
        const val threshold = 0.85F
        const val nmsLimit = 3
    }

    private fun nonMaxSuppression(
        boxes: ArrayList<Result>,
        limit: Int,
        threshold: Float
    ): ArrayList<Result> {
        val scores = boxes.sortedByDescending { it.score }
        val selected = ArrayList<Result>()
        val active = BooleanArray(scores.size) { true }
        var numActive = active.size

        var done = false
        for (i in scores.indices) {
            if (done) break
            if (active[i]) {
                val box1 = scores[i]
                selected.add(box1)
                if (selected.size >= limit) break

                for (j in i + 1 until scores.size) {
                    if (active[j]) {
                        val box2 = scores[j]
                        if (intersectionOverUnion(box1.rect, box2.rect) > threshold) {
                            active[j] = false
                            --numActive
                            if (numActive <= 0) {
                                done = true
                                break
                            }
                        }
                    }
                }
            }
        }
        return selected
    }

    private fun intersectionOverUnion(rect1: Rect, rect2: Rect): Float {
        val area1 = (rect1.right - rect1.left) * (rect1.bottom - rect1.top)
        if (area1 <= 0F) {
            return 0F
        }

        val area2 = (rect2.right - rect2.left) * (rect2.bottom - rect2.top)
        if (area2 <= 0F) {
            return 0F
        }

        val intersectionMinX = max(rect1.left, rect2.left)
        val intersectionMinY = max(rect1.top, rect2.top)
        val intersectionMaxX = min(rect1.right, rect2.right)
        val intersectionMaxY = min(rect1.bottom, rect2.bottom)
        val intersectionArea = max(intersectionMaxY - intersectionMinY, 0) *
                max(intersectionMaxX - intersectionMinX, 0)

        return intersectionArea.toFloat() / (area1 + area2 - intersectionArea)
    }

    override fun outputsToNMSPredictions(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        ivScaleX: Float,
        ivScaleY: Float,
        startX: Float,
        startY: Float
    ): ArrayList<Result> {
        val results = ArrayList<Result>()
        for (i in 0 until outputRow) {
            var classIndex = 0
            var score = outputs[i * outputColumn + 4]
            for (j in 0 until outputColumn - 4) {
                if (outputs[i * outputColumn + 4 + j] > score) {
                    score = outputs[i * outputColumn + 4 + j]
                    classIndex = j
                }
            }
            if (score > threshold) {
                val x = outputs[i * outputColumn]
                val y = outputs[i * outputColumn + 1]
                val w = outputs[i * outputColumn + 2]
                val h = outputs[i * outputColumn + 3]

                val left = imgScaleX * (x - w / 2)
                val top = imgScaleY * (y - h / 2)
                val right = imgScaleX * (x + w / 2)
                val bottom = imgScaleY * (y + h / 2)

                val rect = Rect(
                    (startX + ivScaleX * left).toInt(),
                    (startY + ivScaleY * top).toInt(),
                    (startX + ivScaleX * right).toInt(),
                    (startY + ivScaleY * bottom).toInt()
                )

                val result = Result(
                    classIndex = classIndex,
                    score = score,
                    rect
                )
                results.add(result)
            }
        }
        return nonMaxSuppression(boxes = results, limit = nmsLimit, threshold = 0.5F)
    }
}