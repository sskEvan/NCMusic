package com.ssk.ncmusic.ui.common.refresh.indicator.header

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ssk.ncmusic.core.viewstate.LoadingComponent
import com.ssk.ncmusic.ui.common.refresh.SwipeRefreshState
import com.ssk.ncmusic.ui.common.refresh.SwipeRefreshStateType
import com.ssk.ncmusic.ui.common.refresh.indicator.ArrowDrawable
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

private val IndicatorHeight = 60.dp

/**
 * Created by ssk on 2022/1/10.
 * Description-> 经典下拉刷新header
 */
@Composable
fun CommonSwipeRefreshIndicator(
    state: SwipeRefreshState,
    refreshTrigger: Float,
    maxDrag: Float
) {


    val indicatorHeight = with(LocalDensity.current) { IndicatorHeight.toPx() }
    val offset = (maxDrag - indicatorHeight).coerceAtMost(state.indicatorOffset - indicatorHeight)

    //Log.e("ssk", "indicatorHeight=${indicatorHeight},state.indicatorOffset=${state.indicatorOffset},refreshTrigger=${refreshTrigger},offset=${offset}")

    val releaseToRefresh = offset > refreshTrigger - indicatorHeight

    val text = when (state.type) {
        SwipeRefreshStateType.IDLE -> if (releaseToRefresh) "释放刷新" else "下拉刷新"
        SwipeRefreshStateType.REFRESHING -> "正在刷新..."
        SwipeRefreshStateType.SUCCESS -> "刷新成功"
        SwipeRefreshStateType.FAIL -> "刷新失败"
    }
    val angle = remember {
        Animatable(0f)
    }
    LaunchedEffect(releaseToRefresh) {
        if (releaseToRefresh) {
            angle.animateTo(180f)
        } else {
            angle.animateTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .offset { IntOffset(0, offset.toInt()) },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state.isRefreshing()) {
                LoadingComponent(
                    modifier = Modifier.wrapContentSize(),
                    loadingWidth = 30.cdp,
                    loadingHeight = 30.cdp,
                    color = AppColorsProvider.current.secondIcon)
            } else if (state.isIdle()) {
                Icon(
                    painter = rememberDrawablePainter(ArrowDrawable()),
                    contentDescription = "",
                    modifier = Modifier
                        .size(28.cdp)
                        .rotate(angle.value),
                    tint = AppColorsProvider.current.secondText
                )
            }
            Text(
                text = text,
                fontSize = 30.csp,
                color = AppColorsProvider.current.secondText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .wrapContentSize()
                    .clipToBounds()
                    .padding(start = 20.cdp)
            )
        }
    }
}
