package com.ssk.ncmusic.ui.page.playmusic.component

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.core.*
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
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonLocalImage
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.common.LifeCycleObserverComponent
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
    DiskRoundBackground()
    DiskPager()
    DiskNeedle()
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

private var onStopBefore = false

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DiskPager() {
    Log.d("ssk", "DiskPager recompose")
    val pagerState = rememberPagerState(
        initialPage = MusicPlayController.curRealIndex,
        pageCount = MusicPlayController.realSongList.size,
        infiniteLoop = true
    )
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    LifeCycleObserverComponent(lifeCycleListener = object : ComposeLifeCycleListener {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if (onStopBefore) {
                onStopBefore = false
                Log.d("ssk", "DiskPager onResume")
                coroutineScope.launch {
                    delay(300)
                    controlSheetNeedleAndDiskAnim(viewModel)
                }
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            onStopBefore = true
            Log.d("ssk", "DiskPager onStop")
            coroutineScope.launch {
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
                viewModel.sheetDiskRotate.stop()
            }
        }
    }) {
        LaunchedEffect(MusicPlayController.isPlaying()) {
            controlSheetNeedleAndDiskAnim(viewModel)
        }

        LaunchedEffect(MusicPlayController.curRealIndex) {
            if (MusicPlayController.curRealIndex != -1 && MusicPlayController.curRealIndex != pagerState.currentPage) {
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
                if (abs(MusicPlayController.curRealIndex - pagerState.currentPage) == 1) {
                    // 左滑/右滑1页
                    pagerState.animateScrollToPage(
                        MusicPlayController.curRealIndex,
                        animationSpec = tween(400)
                    )
                } else {
                    if (MusicPlayController.curRealIndex - pagerState.currentPage == MusicPlayController.realSongList.size - 1) {
                        Log.e("ssk2", "最后到第一， 右滑")
                        pagerState.animateScrollBy(Resources.getSystem().displayMetrics.widthPixels.toFloat())
                        pagerState.scrollToPage(MusicPlayController.curRealIndex)
                    } else if (pagerState.currentPage - MusicPlayController.curRealIndex == MusicPlayController.realSongList.size - 1) {
                        Log.e("ssk2", "第一到最后， 左滑")
                        pagerState.animateScrollBy(-Resources.getSystem().displayMetrics.widthPixels.toFloat())
                        pagerState.scrollToPage(MusicPlayController.curRealIndex)
                    } else {
                        pagerState.scrollToPage(MusicPlayController.curRealIndex)
                    }
                }
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            if (MusicPlayController.curRealIndex != pagerState.currentPage) {
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
                MusicPlayController.playByRealIndex(pagerState.currentPage)
            }
        }

        HorizontalPager(
            modifier = Modifier
                .padding(top = 208.cdp)
                .fillMaxWidth()
                .height(570.cdp),
            state = pagerState,
        ) { position ->
            DiskItem(MusicPlayController.realSongList[position])
        }
    }
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DiskItem(song: SongBean) {
    Log.d("ssk", "-------------DiskItem recompose")
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var dragAmountX = remember { 0f }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onClick(enableRipple = false) {
                Log.d("ssk3", "-------------DiskItem onCLick")
                viewModel.showLyric = !viewModel.showLyric
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
