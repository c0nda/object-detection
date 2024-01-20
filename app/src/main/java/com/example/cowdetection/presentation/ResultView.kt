package com.example.cowdetection.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.cowdetection.utils.prepostprocessor.model.AnalysisResult
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.min

class ResultView constructor(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var results: AnalysisResult? = null

    private val paintRectangle = Paint()
    private val paintText = Paint()

    companion object {
        const val TEXT_X = 10
        const val TEXT_Y = 35
        const val TEXT_WIDTH = 200
        const val TEXT_HEIGHT = 50
//        val classes = listOf(
//            "coverall",
//            "gloves",
//            "helmet",
//            "mask_weared_incorrect",
//            "uniform",
//            "with_hat",
//            "with_mask",
//            "without_hat",
//            "without_mask"
//        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (results == null) return

        val classes = ArrayList<String>()
        val br = BufferedReader(InputStreamReader(context.assets.open("classes.txt")))
        var line: String?
        while (true) {
            line = br.readLine()
            if (line == null) {
                break
            }
            classes.add(line)
        }

        for (result in results!!.results) {
            paintRectangle.strokeWidth = 5F
            paintRectangle.style = Paint.Style.STROKE
            canvas.drawRect(result.rect, paintRectangle)

            val rectF = RectF(
                result.rect.left.toFloat(),
                result.rect.top.toFloat(),
                min((result.rect.left + TEXT_WIDTH).toFloat(), result.rect.right.toFloat()),
                (result.rect.top + TEXT_HEIGHT).toFloat()
            )

            val path = Path()
            path.addRect(rectF, Path.Direction.CW)
            paintText.color = Color.BLUE
            canvas.drawPath(path, paintText)

            paintText.color = Color.WHITE
            paintText.strokeWidth = 0F
            paintText.style = Paint.Style.FILL
            paintText.textSize = 32F

            canvas.drawText(
                String.format("%s %.2f", classes[result.classIndex], result.score),
                (result.rect.left + TEXT_X).toFloat(),
                (result.rect.top + TEXT_Y).toFloat(),
                paintText
            )
        }
    }
}