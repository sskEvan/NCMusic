package com.ssk.ncmusic.utils

import android.graphics.*
import java.lang.Exception

object BitmapUtil {

    //生成圆角图片
    fun getRoundedCornerBitmap(bitmap: Bitmap, roundPx: Int): Bitmap {
        return try {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            val rectF = RectF(
                Rect(
                    0, 0, bitmap.width,
                    bitmap.height
                )
            )
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = Color.BLACK
            canvas.drawRoundRect(rectF, roundPx.toFloat(), roundPx.toFloat(), paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val src = Rect(
                0, 0, bitmap.width,
                bitmap.height
            )
            canvas.drawBitmap(bitmap, src, rect, paint)
            output
        } catch (e: Exception) {
            bitmap
        }
    }
}