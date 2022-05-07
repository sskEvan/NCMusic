package com.ssk.ncmusic.ui.page.mine.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.ScreenUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import kotlinx.coroutines.delay

/**
 * Created by ssk on 2022/5/7.
 */
@Composable
fun CpnSongPlayListHelper() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "歌单助手",
            color = AppColorsProvider.current.secondText,
            fontSize = 28.csp,
            modifier = Modifier.padding(bottom = 12.dp, top = 20.cdp, start = 32.cdp)
        )

        Text(
            text = "你可以从歌单中筛选出",
            color = AppColorsProvider.current.secondText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 10.cdp, bottom = 24.cdp)
                .fillMaxWidth(),
        )

        RollTextLayout()

        Button(
            onClick = {},
            modifier = Modifier
                .padding(start = 160.cdp, end = 160.cdp, bottom = 40.cdp, top = 20.cdp)
                .fillMaxWidth()
                .height(70.cdp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColorsProvider.current.primary
            ),
            content = {
                Text(text = "试一下", fontSize = 30.csp, color = Color.White)
            }
        )
    }
}

@Composable
private fun RollTextLayout() {
    var topIndex by remember { mutableStateOf(0) }
    var middleIndex by remember { mutableStateOf(1) }
    var bottomIndex by remember { mutableStateOf(2) }
    var doAnim by remember { mutableStateOf(false) }
    var resetAnim by remember { mutableStateOf(true) }
    val animState by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(doAnim, resetAnim) {
        if (doAnim && resetAnim) {
            animState.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 800
                    0f at 0
                    1f at 800
                }
            )
        } else {
            animState.stop()
        }
    }

    LaunchedEffect(animState.value == 1f) {
        if (animState.value == 1f) {
            resetAnim = false
            delay(3000)

            topIndex = if (topIndex == mSongLabels.size - 1) 0 else topIndex + 1
            middleIndex = if (middleIndex == mSongLabels.size - 1) 0 else middleIndex + 1
            bottomIndex = if (bottomIndex == mSongLabels.size - 1) 0 else bottomIndex + 1

            animState.snapTo(0f)
            resetAnim = true
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .height(160.cdp)
            .onGloballyPositioned {
                doAnim = it.positionInWindow().y < ScreenUtil.getScreenHeight()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var topAnimValue = animState.value
        if (animState.value > 0.5f) {
            topAnimValue = 0.5f
        }
        topAnimValue = 1 - topAnimValue * 2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.cdp)
                .graphicsLayer {
                    alpha = topAnimValue
                    scaleY = topAnimValue
                    scaleX = topAnimValue
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = getSpanText(mSongLabels[topIndex]))
        }

        Box {
            var middleAnimValue = animState.value
            if (middleAnimValue <= 0.5f) {
                middleAnimValue = 0.5f
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.cdp)
                    .graphicsLayer {
                        alpha = middleAnimValue
                        scaleY = middleAnimValue
                        scaleX = middleAnimValue
                        translationY = if (animState.value != 0f) {
                            -80.cdp.toPx() * animState.value
                        } else {
                            0f
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = getSpanText(mSongLabels[middleIndex]))
            }

            if (animState.value >= 0.5f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.cdp)
                        .graphicsLayer {
                            alpha = animState.value - 0.5f
                            scaleY = animState.value - 0.5f
                            scaleX = animState.value - 0.5f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getSpanText(mSongLabels[bottomIndex]))
                }
            }
        }

    }
}


private fun getSpanText(sourceText: String): AnnotatedString {

    return buildAnnotatedString {
        sourceText.split(" ").forEachIndexed() { index, it ->
            var ss = ""
            if (it.contains("的")) {
                ss = it
                withStyle(
                    style = SpanStyle(
                        color = Color.LightGray,
                        fontSize = (32 * 0.9).csp,
                    ),
                ) {
                    append(" $ss ")
                }
            } else {
                ss = " $it "
                withStyle(
                    style = SpanStyle(
                        color = Color.LightGray,
                        fontSize = 32.csp,
                    ),
                ) {
                    append(" ")
                }
                withStyle(
                    style = SpanStyle(
                        color = FOREGROUND_COLORS[index % FOREGROUND_COLORS.size],
                        background = BACKGROUND_COLORS[index % BACKGROUND_COLORS.size],
                        fontSize = 32.csp
                    )
                ) {
                    append(" $ss ")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.LightGray,
                        fontSize = 32.csp,
                    ),
                ) {
                    append(" ")
                }
            }
        }
    }
}


private val BACKGROUND_COLORS = listOf(
    Color(0xFFE6F7FF),
    Color(0xFFFBEFFF),
    Color(0xFFFFFBE6),
    Color(0xFFFFF3F3)
)
private val FOREGROUND_COLORS = listOf(
    Color(0xFF1890FF),
    Color(0xFFCA72E7),
    Color(0xFFFAAD14),
    Color(0xFFEA4C43)
)

private var mSongLabels = listOf(
    "80年代 华语 老歌",
    "最近收藏 的 清新民谣",
    "适合 夜晚听 的 纯音乐",
    "很久没听 的 后摇",
    "最近一年 发布 的 流行音乐"
//    "1111 1111 1111",
//    "2222 2222 2222",
//    "3333 3333 3333",
//    "4444 4444 4444",
//    "5555 5555 5555",
)