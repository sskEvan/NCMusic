package com.ssk.ncmusic.ui.page.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun DiscoveryPage() {
    Column(
        Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        CommonTopAppBar(title = "发现", leftIconResId = -1)
        Text("发现")

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "测试数据测试数据",
                color = Color.White,
                fontSize = 40.csp,
                textAlign = TextAlign.Center,
                modifier = Modifier.drawWithContent {
                    val colors = listOf(Color.White, Color.Transparent)
                    val paint = Paint().asFrameworkPaint()
                    drawIntoCanvas {
                        val layerId: Int = it.nativeCanvas.saveLayer(
                            0f,
                            0f,
                            size.width,
                            size.height,
                            paint)
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                        it.nativeCanvas.restoreToCount(layerId)
                    }
                }
            )
        }
    }
}