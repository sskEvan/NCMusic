package com.ssk.ncmusic.ui.page.playmusic.component

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonLocalImage
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.page.playmusic.DISK_ROTATE_ANIM_CYCLE
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.viewmodel.playmusic.PlayMusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Created by ssk on 2022/5/11.
 */
@Composable
fun CpnDiskPager() {
    val viewModel: PlayMusicViewModel = hiltViewModel()

    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(true) }
            .apply { targetState = !viewModel.showLyric },
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            DiskRoundBackground()
            DiskPager()
            DiskNeedle()
        }
    }
}

@Composable
private fun DiskRoundBackground() {
    // 半透明圆形背景
    Box(
        modifier = Modifier
            .padding(top = 208.cdp)
            .width(570.cdp)
            .height(570.cdp)
            .clip(CircleShape)
            .background(Color(0x55EEEEEE))
    )
}

private const val MAX_PAGE_COUNT = 1000000

@Composable
private fun DiskNeedle() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val needleRotateAnim by animateFloatAsState(
        targetValue = if (viewModel.sheetNeedleUp) -25f else 0f,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing)
    )
    Image(
        painter = painterResource(id = R.drawable.ic_play_neddle),
        contentDescription = "needle",
        modifier = Modifier
            .padding(start = 146.cdp)
            .width(228.cdp)
            .height(348.cdp)
            .graphicsLayer(
                rotationZ = needleRotateAnim,
                transformOrigin = TransformOrigin(0.164f, 0.109f)
            )
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DiskPager() {
    // 新版HorizontalPager无限循环滑动实现，参考https://github.com/google/accompanist/blob/main/sample/src/main/java/com/google/accompanist/sample/pager/HorizontalPagerLoopingSample.kt
    val middleIndex = MAX_PAGE_COUNT / 2
    val pageCount = MusicPlayController.realSongList.size
    Log.d("ssk2", "DiskPager recompose pageCount = $pageCount")

    val pagerState = rememberPagerState(
        initialPage = middleIndex + MusicPlayController.curRealIndex
    )
    val viewModel: PlayMusicViewModel = hiltViewModel()

    LaunchedEffect(MusicPlayController.isPlaying()) {
        controlSheetNeedleAndDiskAnim(viewModel)
    }

    LaunchedEffect(MusicPlayController.curRealIndex) {
        val curPage = pagerState.currentPage
        val curPageFloorMod = (pagerState.currentPage - middleIndex).floorMod(pageCount)
        if (MusicPlayController.curRealIndex != -1 && MusicPlayController.curRealIndex != curPageFloorMod) {
            viewModel.lastSheetDiskRotateAngleForSnap = 0f
            viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
            if (abs(MusicPlayController.curRealIndex - curPageFloorMod) == 1) {
                // 左滑/右滑1页
                pagerState.animateScrollToPage(curPage + MusicPlayController.curRealIndex - curPageFloorMod)
            } else {
                if (MusicPlayController.curRealIndex - curPageFloorMod == MusicPlayController.realSongList.size - 1) {
                    Log.e("ssk2", "最后到第一， 右滑")
                    pagerState.animateScrollBy(-Resources.getSystem().displayMetrics.widthPixels.toFloat())
                } else if (curPageFloorMod - MusicPlayController.curRealIndex == MusicPlayController.realSongList.size - 1) {
                    Log.e("ssk2", "第一到最后， 左滑")
                    pagerState.animateScrollBy(Resources.getSystem().displayMetrics.widthPixels.toFloat())
                } else {
                    pagerState.scrollToPage(curPage + MusicPlayController.curRealIndex - curPageFloorMod)
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val curPageFloorMod = (pagerState.currentPage - middleIndex).floorMod(pageCount)
        if (MusicPlayController.curRealIndex != curPageFloorMod) {
            viewModel.lastSheetDiskRotateAngleForSnap = 0f
            viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
            MusicPlayController.playByRealIndex(curPageFloorMod)
        }
    }


    HorizontalPager(
        count = MAX_PAGE_COUNT,
        modifier = Modifier
            .padding(top = 208.cdp)
            .fillMaxWidth()
            .height(570.cdp),
        state = pagerState,
    ) { position ->
        val page = (position - middleIndex).floorMod(pageCount)
        DiskItem(MusicPlayController.realSongList[page])
    }

}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

private suspend fun controlSheetNeedleAndDiskAnim(viewModel: PlayMusicViewModel) {
    Log.e("ssk", "controlSheetNeedleAndDiskAnim isPlaying=${MusicPlayController.isPlaying()}")
    if (MusicPlayController.isPlaying()) {
        Log.e("ssk", "controlSheetNeedleAndDiskAnim start")
        viewModel.sheetNeedleUp = false
        viewModel.sheetDiskRotate.stop()
        viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
        viewModel.sheetDiskRotate.animateTo(
            targetValue = 360f + viewModel.lastSheetDiskRotateAngleForSnap,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = DISK_ROTATE_ANIM_CYCLE, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        Log.e("ssk", "controlSheetNeedleAndDiskAnim end")
    } else {
        viewModel.sheetNeedleUp = true
    }
}

@Composable
private fun DiskItem(song: SongBean) {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var dragAmountX = remember { 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onClick(enableRipple = false) {
                Log.d("ssk3", "-------------DiskItem onCLick")
                scope.launch {
                    viewModel.lastSheetDiskRotateAngleForSnap =
                        viewModel.sheetDiskRotate.value
                    viewModel.sheetDiskRotate.stop()
                    viewModel.showLyric = !viewModel.showLyric
                }
            }
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Final)
                            if (event.changes.size == 1) {
                                val pointer = event.changes[0]
                                if (pointer.pressed) {
                                    dragAmountX += pointer.position.x - pointer.previousPosition.x
                                    if (abs(dragAmountX) >= 6f) {
                                        if (!viewModel.sheetNeedleUp) {
                                            scope.launch {
                                                viewModel.lastSheetDiskRotateAngleForSnap =
                                                    viewModel.sheetDiskRotate.value
                                                viewModel.sheetDiskRotate.stop()
                                            }
                                        }
                                        viewModel.sheetNeedleUp = true
                                    }
                                } else if (!pointer.pressed) {
                                    dragAmountX = 0f
                                    scope.launch {
                                        delay(400)
                                        if (MusicPlayController.isPlaying() && viewModel.sheetNeedleUp) {
                                            viewModel.sheetNeedleUp = false
                                            viewModel.sheetDiskRotate.animateTo(
                                                targetValue = 360f + viewModel.lastSheetDiskRotateAngleForSnap,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(
                                                        durationMillis = DISK_ROTATE_ANIM_CYCLE,
                                                        easing = LinearEasing
                                                    ),
                                                    repeatMode = RepeatMode.Restart
                                                )
                                            )
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
            }
            .graphicsLayer {
                rotationZ =
                    if (song.id == MusicPlayController.realSongList[MusicPlayController.curRealIndex].id) viewModel.sheetDiskRotate.value else 0f
            },
        contentAlignment = Alignment.Center
    ) {
        CommonLocalImage(
            R.drawable.ic_disc,
            modifier = Modifier
                .width(562.cdp)
                .height(562.cdp)
        )

        CommonNetworkImage(
            url = song.al.picUrl,
            modifier = Modifier
                .width(374.cdp)
                .height(374.cdp)
                .clip(CircleShape)
                .border(
                    width = 4.cdp,
                    color = Color.Black,
                    shape = CircleShape
                ),
            placeholder = R.drawable.ic_default_disk_cover,
            error = R.drawable.ic_default_disk_cover,
        )
    }
}
