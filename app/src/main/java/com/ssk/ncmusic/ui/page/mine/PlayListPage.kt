package com.ssk.ncmusic.ui.page.mine

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssk.ncmusic.R
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.common.CommonHeadBackgroundShape
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import kotlinx.coroutines.launch
import me.onebone.toolbar.*

/**
 * Created by ssk on 2022/4/21.
 */
@Composable
fun PlaylistPage(playlistBean: PlaylistBean) {

    val sysUiController = rememberSystemUiController()
    sysUiController.setSystemBarsColor(
        color = Color.Transparent, !isSystemInDarkTheme()
    )

    val state = rememberCollapsingToolbarScaffoldState()
    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            ScrollHeader(playlistBean, state)
        }
    ) {
        Body()
    }

}

var doAnim = false

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun CollapsingToolbarScope.ScrollHeader(playlistBean: PlaylistBean, toolbarState: CollapsingToolbarScaffoldState) {
    Log.e("ssk", "toolbarState=${toolbarState.toolbarState.progress},isScrollInProgress=${toolbarState.toolbarState.isScrollInProgress}")
//    val scope = rememberCoroutineScope()
//    if(!toolbarState.toolbarState.isScrollInProgress && !doAnim) {
//        if(toolbarState.toolbarState.progress <= 0.5f && toolbarState.toolbarState.progress > 0) {
//            doAnim = true
//            scope.launch {
//                toolbarState.toolbarState.collapse(100)
//                doAnim =false
//            }
//        }else if(toolbarState.toolbarState.progress > 0.5 && toolbarState.toolbarState.progress < 1) {
//            doAnim = true
//            scope.launch {
//                toolbarState.toolbarState.expand(100)
//                doAnim = false
//            }
//        }
//    }
    Box(
        modifier = Modifier
            .parallax(1f)
            .fillMaxWidth()
            .height(584.cdp)
            .clip(CommonHeadBackgroundShape(toolbarState.toolbarState.progress * 80))
            .background(brush = Brush.linearGradient(listOf(Color.LightGray.copy(0.5f), Color.Gray.copy(0.5f), Color.LightGray.copy(0.5f))))
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            rememberImagePainter(playlistBean.coverImgUrl,
                builder = {
                    transformations(BlurTransformation(LocalContext.current, 10f, 10f))
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(584.cdp)
                .graphicsLayer { alpha = 0.5f },
        )
    }

    CommonTopAppBar(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(88.cdp),
        backgroundColor = Color.Transparent,
        title = "歌单",
        contentColor = AppColorsProvider.current.pure,
        leftIconResId = R.drawable.ic_drawer_toggle,
        leftClick = { },
        rightIconResId = R.drawable.ic_search
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Body() {
    CompositionLocalProvider(LocalOverScrollConfiguration.provides(null)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())) {
            for (i in 1 until  20) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.cdp),
                    contentAlignment = Alignment.Center) {
                    Text(text = "$i")
                }
            }
        }
    }

}