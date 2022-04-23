package com.ssk.ncmusic.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.min

/**
 * Created by ssk on 2022/4/23.
 */
@Composable
fun CircleProgress(modifier: Modifier = Modifier, progress: Int) {
    val sweepAngle = progress / 100f * 360
    Canvas(modifier = modifier) {
        val canvasSize = min(size.width, size.height)
        drawCircle(color = Color.LightGray, radius = canvasSize / 2, style = Stroke(width = 4f))
        drawArc(color = Color.DarkGray, style = Stroke(width = 4f), startAngle = -90f, sweepAngle = sweepAngle, useCenter = false)
    }
}
