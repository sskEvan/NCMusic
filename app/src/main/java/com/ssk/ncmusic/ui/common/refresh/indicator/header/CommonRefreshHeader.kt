package com.ssk.ncmusic.ui.common.refresh.indicator.header

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.ssk.ncmusic.core.viewstate.LoadingComponent
import com.ssk.ncmusic.ui.common.refresh.indicator.ArrowDrawable
import com.ssk.ncmusic.ui.common.swipe.RefreshType
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.FormatterEnum
import com.ssk.ncmusic.utils.TimeUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import java.util.*

/**
 * Created by ssk on 2022/1/10.
 * Description-> 经典上拉加载更多footer
 */
@Composable
fun CommonRefreshHeader(refreshType: RefreshType) {

    val tip = when (refreshType) {
        RefreshType.PULL_TO_REFRESH -> "下拉刷新"
        RefreshType.RELEASE_TO_REFRESH -> "释放刷新"
        RefreshType.REFRESHING -> "正在刷新..."
        RefreshType.REFRESH_SUCCESS -> "刷新成功"
        RefreshType.REFRESH_FAIL -> "刷新失败"
        else -> "下拉刷新"
    }

    val angle = remember {
        Animatable(0f)
    }

    var lastRefreshTime by remember {
        mutableStateOf(TimeUtil.parse(Date().time, FormatterEnum.YYYY_MM_DD__HH_MM))
    }

    LaunchedEffect(refreshType) {
        if (refreshType == RefreshType.PULL_TO_REFRESH) {
            angle.animateTo(180f)
        } else if (refreshType == RefreshType.RELEASE_TO_REFRESH) {
            angle.animateTo(0f)
        } else if (refreshType == RefreshType.REFRESH_SUCCESS) {
            lastRefreshTime = TimeUtil.parse(Date().time, FormatterEnum.YYYY_MM_DD__HH_MM)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.cdp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (refreshType == RefreshType.REFRESHING) {
                LoadingComponent(
                    modifier = Modifier.wrapContentSize(),
                    loadingWidth = 36.cdp,
                    loadingHeight = 36.cdp,
                    color = AppColorsProvider.current.secondIcon
                )
            } else if (refreshType == RefreshType.REFRESH_SUCCESS || refreshType == RefreshType.REFRESH_FAIL) {
                Box(modifier = Modifier.size(36.cdp))
            } else {
                val nativeColorInt = AppColorsProvider.current.secondIcon.toArgb()
                val nativeColor = android.graphics.Color.argb(
                    nativeColorInt.alpha,
                    nativeColorInt.red,
                    nativeColorInt.blue,
                    nativeColorInt.green
                )
                Image(
                    painter = rememberDrawablePainter(ArrowDrawable().apply {
                        setColor(nativeColor)
                    }),
                    contentDescription = "",
                    modifier = Modifier
                        .size(36.cdp)
                        .rotate(angle.value)
                )
            }
            Column(
                modifier = Modifier.padding(32.cdp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tip,
                color = AppColorsProvider.current.secondText,
                    textAlign = TextAlign.Center,
                    fontSize = 32.csp,
                    modifier = Modifier
                        .wrapContentSize()
                        .clipToBounds()
                )
                Text(
                    text = "上次更新 $lastRefreshTime",
                color = AppColorsProvider.current.secondText,
                    textAlign = TextAlign.Center,
                    fontSize = 24.csp,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 4.cdp)
                )
            }
            Box(modifier = Modifier.size(36.cdp))
        }
    }
}
