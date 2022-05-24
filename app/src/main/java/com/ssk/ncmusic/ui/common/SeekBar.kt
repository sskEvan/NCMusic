package com.ssk.ncmusic.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.ssk.ncmusic.utils.cdp

/**
 * Created by ssk on 2022/4/23.
 */
private val progressPaint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
}

private val circlePaint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
}

var width = 0f
var height = 0f

@Composable
fun SeekBar(
    progress: Int = 0,
    seeking: (Int) -> Unit = {},
    seekTo: (Int) -> Unit = {},
    smallRadius: Float = 10f,
    largeRadius: Float = 20f,
    progressHeight: Float = 4f,
    progressColor: Color = Color.LightGray,
    circleColor: Color = Color.White,
    modifier: Modifier = Modifier
) {

    var isPressed by remember {
        mutableStateOf(false)
    }

    var circleCenterX by remember {
        mutableStateOf(0f)
    }

    progressPaint.color = progressColor
    circlePaint.color = circleColor

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        while (true) {
                            val event: PointerEvent = awaitPointerEvent(PointerEventPass.Main)
                            if (event.changes.size == 1) {
                                // 1.单指操作
                                val pointer = event.changes[0]
                                val x = pointer.position.x
                                circleCenterX = pointer.position.x

                                if (x < 0f) {
                                    circleCenterX = 0f
                                } else if (x > width) {
                                    circleCenterX = width
                                } else {
                                    circleCenterX = x
                                }
                                seeking.invoke((circleCenterX * 100 / width + 0.5).toInt())

                                if (!pointer.pressed) {
                                    // 手指抬起,结束
                                    isPressed = false
                                    seekTo.invoke((circleCenterX * 100 / width + 0.5).toInt())
                                    break
                                } else {
                                    if (!pointer.previousPressed) {
                                        // 按下
                                        isPressed = true
                                    }
                                }
                                pointer.consume()
                            }
                        }
                    }
                }
            }) {
            width = drawContext.size.width
            height = drawContext.size.height
            drawIntoCanvas {
                val rect = Rect(
                    Offset(0f, (height - progressHeight) / 2),
                    Offset(width, (height + progressHeight) / 2)
                )
                it.drawRect(rect, progressPaint)

                var x = width * progress / 100
                val radius = if (isPressed) largeRadius else smallRadius
                if (x < radius) {
                    x = radius
                } else if (x > width - radius) {
                    x = width - radius
                }
                it.drawCircle(
                    Offset(x, height / 2),
                    radius,
                    circlePaint
                )
            }
        }
    }
}

