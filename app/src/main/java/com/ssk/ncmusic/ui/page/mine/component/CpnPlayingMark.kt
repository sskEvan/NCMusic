package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp

/**
 * Created by ssk on 2022/4/25.
 */

@Composable
fun CpnPlayingMark(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    playing: Boolean = false
) {
    val color = AppColorsProvider.current.primary

    val anim by remember {
        mutableStateOf(Animatable(0.4f))
    }

    LaunchedEffect(playing) {
        if (playing) {
            anim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            anim.stop()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {

        Canvas(
            modifier = Modifier
                .width(32.cdp)
                .height(32.cdp)
        ) {
            val rectWidth = size.width / 5
            val canvasHeight = size.height

            val rectHeight1 = if (playing) {
                canvasHeight * (0.75f - anim.value * 0.75f + 0.25f)
            } else {
                canvasHeight * 0.7f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(16.cdp.toPx()),
                topLeft = Offset(0f, canvasHeight - rectHeight1),
                size = Size(rectWidth, rectHeight1)
            )

            val rectHeight2 = if (playing) {
                canvasHeight * (anim.value * 0.65f + 0.2f)
            } else {
                canvasHeight * 0.9f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(16.cdp.toPx()),
                topLeft = Offset(rectWidth * 2, canvasHeight - rectHeight2),
                size = Size(rectWidth, rectHeight2)
            )


            val rectHeight3 = if (playing) {
                canvasHeight * (0.6f - anim.value * 0.6f + 0.4f)
            } else {
                canvasHeight * 0.5f
            }
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(16.cdp.toPx()),
                topLeft = Offset(rectWidth * 4, canvasHeight - rectHeight3),
                size = Size(rectWidth, rectHeight3)
            )
        }
    }
}

