package com.ssk.ncmusic.ui.page.playmusic.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.model.LyricContributorBean
import com.ssk.ncmusic.model.LyricResult
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.utils.transformDp
import com.ssk.ncmusic.viewmodel.playmusic.LyricModel
import com.ssk.ncmusic.viewmodel.playmusic.PlayMusicViewModel

/**
 * Created by ssk on 2022/5/11.
 */
@Composable
fun CpnLyric() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(false) }
            .apply { targetState = viewModel.showLyric },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        var cpnLyricHeight  by remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    cpnLyricHeight = it.size.height
                }
                .onClick(enableRipple = false) {
                    viewModel.showLyric = !viewModel.showLyric
                },
            contentAlignment = Alignment.Center
        ) {
            ViewStateComponent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 50.cdp),
                viewStateLiveData = viewModel.lyricResult,
                customLoadingComponent = {
                    ViewStateTip("加载歌词中...")
                },
                customEmptyComponent = {
                    ViewStateTip("暂无歌词")
                },
                customFailComponent = {
                    ViewStateTip("加载歌词出错, 点击重试", true)
                },
                customErrorComponent = {
                    ViewStateTip("加载歌词出错, 点击重试", true)
                }
            ) { data ->
                LyricList(data, cpnLyricHeight)
            }
        }
    }
}

@Composable
private fun ViewStateTip(tip: String, enableRetry: Boolean = false) {
    val viewModel: PlayMusicViewModel = hiltViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onClick(enableRipple = enableRetry) {
                if (enableRetry) {
                    viewModel.getLyric(MusicPlayController.realSongList[MusicPlayController.curRealIndex])
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = tip, color = Color.White, fontSize = 30.csp)
    }
}

@Composable
private fun LyricList(data: LyricResult, cpnLyricHeight: Int) {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val lazyListState = rememberLazyListState()
    LaunchedEffect(viewModel.curLyricIndex) {
        if (viewModel.curLyricIndex >= 0) {
            lazyListState.animateScrollToItem(viewModel.curLyricIndex)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                val paint = Paint().asFrameworkPaint()
                drawIntoCanvas {
                    val layerId: Int = it.nativeCanvas.saveLayer(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        paint
                    )
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            Pair(0f, Color.Transparent),
                            Pair(0.15f, Color.White),
                            Pair(0.85f, Color.White),
                            Pair(1f, Color.Transparent)
                        ),
                        blendMode = BlendMode.DstIn
                    )
                    it.nativeCanvas.restoreToCount(layerId)
                }
            },
        state = lazyListState,
        contentPadding = PaddingValues(vertical = (cpnLyricHeight * 0.4).transformDp)
    ) {

        itemsIndexed(viewModel.lyricModelList) { index, item ->
            LyricItem(index, item, viewModel)
        }
        item {
            LyricConstructorInfo(data.lyricUser, data.transUser)
        }
    }

}

@Composable
private fun LyricConstructorInfo(transUser: LyricContributorBean?, lyricUser: LyricContributorBean?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.cdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        lyricUser?.let {
            Text(
                text = "歌词贡献者：${it.nickname}",
                fontSize = 32.csp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
            )
        }

        transUser?.let {
            Text(
                text = "翻译贡献者：${it.nickname}",
                fontSize = 32.csp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(top = 16.cdp)
            )
        }
    }
}

@Composable
private fun LyricItem(index: Int, lyricModel: LyricModel, viewModel: PlayMusicViewModel) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.cdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        lyricModel.lyric?.let {
            Text(
                text = it,
                fontSize = 32.csp,
                color = if (viewModel.curLyricIndex == index) AppColorsProvider.current.primary else Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        lyricModel.tLyric?.let {
            Text(
                text = it,
                fontSize = 30.csp,
                color = if (viewModel.curLyricIndex == index) AppColorsProvider.current.primary else Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.cdp),
                textAlign = TextAlign.Center
            )
        }
    }
}

