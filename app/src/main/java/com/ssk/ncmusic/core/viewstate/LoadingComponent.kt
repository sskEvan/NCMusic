package com.ssk.ncmusic.core.viewstate

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import kotlin.math.max

/**
 * Created by ssk on 2021/9/15.
 */
@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentAlignment: Alignment = Alignment.Center
) {
    val color = AppColorsProvider.current.primary
    val animateTween by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(400, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {

        Canvas(
            modifier = Modifier
                .padding(30.dp)
                .width(30.dp)
                .height(24.dp)
        ) {
            val rectWidth = size.width / 10
            val canvasHeight = size.height

            val rectHeight1 = max(canvasHeight * 0.2f, canvasHeight * animateTween * 0.9f)
            drawRect(
                color = color,
                topLeft = Offset(0f, canvasHeight - rectHeight1),
                size = Size(rectWidth, rectHeight1)
            )

            val rectHeight2 = canvasHeight - (canvasHeight * animateTween * 0.75f)
            drawRect(
                color = color,
                topLeft = Offset(rectWidth * 3, canvasHeight - rectHeight2),
                size = Size(rectWidth, rectHeight2)
            )


            val rectHeight3 = canvasHeight * animateTween * 1.0f
            drawRect(
                color = color,
                topLeft = Offset(rectWidth * 6, canvasHeight - rectHeight3),
                size = Size(rectWidth, rectHeight3)
            )


            val rectHeight4 = canvasHeight - (canvasHeight * animateTween * 0.85f)
            drawRect(
                color = color,
                topLeft = Offset(rectWidth * 9, canvasHeight - rectHeight4),
                size = Size(rectWidth, rectHeight4)
            )
        }
    }

}