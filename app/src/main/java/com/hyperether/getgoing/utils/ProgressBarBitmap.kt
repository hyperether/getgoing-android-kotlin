package com.hyperether.getgoing.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.hyperether.getgoing.R

class ProgressBarBitmap {

    companion object {
        fun newInstance() = ProgressBarBitmap()
    }

    fun getWidgetBitmap(
        context: Context,
        goal: Long,
        lenght: Double,
        width: Int,
        height: Int,
        startAngle: Float,
        sweepAngle: Float,
        stroke: Int,
        padding: Int
    ): Bitmap {

        var scale: Float = 0f
        if (goal > 0 && lenght >= 0) {
            scale = (lenght / goal).toFloat()
        }

        val paint: Paint =
            Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = stroke.toFloat()
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        val arc = RectF()
        arc[(stroke / 2 + padding).toFloat(), (stroke / 2 + padding).toFloat(), (width - padding - stroke / 2).toFloat()] =
            (height - padding - stroke / 2).toFloat()
        val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)
        paint.setColor(context.resources.getColor(R.color.color_button_background))
        canvas.drawArc(arc, startAngle, sweepAngle, false, paint)
        paint.setColor(context.resources.getColor(R.color.light_theme_accent))
        var temp = 0f
        if (scale <= 1 && scale >= 0) {
            temp = sweepAngle * scale
        } else if (scale < 0) {
            temp = 0f
        } else {
            temp = sweepAngle
        }
        canvas.drawArc(arc, startAngle, temp, false, paint)
        return bitmap
    }
}