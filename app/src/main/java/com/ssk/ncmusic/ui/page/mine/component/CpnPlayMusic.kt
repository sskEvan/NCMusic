package com.ssk.ncmusic.ui.page.mine.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.*
import com.ssk.ncmusic.ui.page.mine.DISK_ROTATE_ANIM_CYCLE
import com.ssk.ncmusic.ui.page.mine.lastSheetDiskRotateAngleForSnap
import com.ssk.ncmusic.ui.page.mine.sheetDiskRotate
import com.ssk.ncmusic.ui.page.mine.sheetNeedleUp
import com.ssk.ncmusic.ui.page.showPlayListSheet
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

/**
 * Created by ssk on 2022/4/25.
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CpnPlayMusic(backCallback: () -> Unit) {
    Log.d("ssk", "PlayMusicContent recompose")
    val pagerState = rememberPagerState(
        initialPage = MusicPlayController.curIndex,
        pageCount = MusicPlayController.songList.size
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.5f),
                        Color.DarkGray.copy(alpha = 0.5f),
                        Color.Black.copy(alpha = 0.5f)
                    )
                )
            )
            .onClick(enableRipple = false) {},
        contentAlignment = Alignment.Center
    ) {
        val curSong = MusicPlayController.songList[MusicPlayController.curIndex]
        BlurBackground(curSong)
        Column(modifier = Modifier.fillMaxSize()) {
            CommonTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                customTitleLayout = {
                    Column(Modifier.fillMaxSize()) {
                        Text(
                            text = curSong.name,
                            fontSize = 36.csp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = curSong.ar[0].name,
                            fontSize = 24.csp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.cdp)
                        )
                    }

                },
                leftIconResId = R.drawable.ic_arrow_down,
                leftClick = { backCallback() },
                backgroundColor = Color.Transparent,
                contentColor = Color.White
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    DiskRoundBackground()
                    DiskPager(pagerState)
                    DiskNeedle()
                }

                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    MiddleActionLayout()
                    ProgressLayout()
                    BottomActionLayout()
                }
            }
        }
    }
}

@Composable
private fun BlurBackground(song: SongBean) {
    // 高斯模糊背景
    Image(
        painter = rememberImagePainter(
            song.al.picUrl,
            builder = {
                transformations(BlurTransformation(LocalContext.current, 18f, 5f))
            }
        ),
        contentDescription = "disc_background",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.5f }
    )
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
    val needleRotateAnim by animateFloatAsState(
        targetValue = if (sheetNeedleUp) -25f else 0f,
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
private fun DiskPager(pagerState: PagerState) {
    Log.d("ssk", "DiskPager recompose")
    val coroutineScope = rememberCoroutineScope()

    LifeCycleObserverComponent(lifeCycleListener = object : ComposeLifeCycleListener {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            if(onStopBefore) {
                onStopBefore = false
                Log.d("ssk", "DiskPager onResume")
                coroutineScope.launch {
                    delay(300)
                    controlSheetNeedleAndDiskAnim()
                }
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            onStopBefore = true
            Log.d("ssk", "DiskPager onStop")
            coroutineScope.launch {
                lastSheetDiskRotateAngleForSnap = 0f
                sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
                sheetDiskRotate.stop()
            }
        }
    }) {
        LaunchedEffect(MusicPlayController.isPlaying()) {
            controlSheetNeedleAndDiskAnim()
        }

        LaunchedEffect(MusicPlayController.curIndex) {
            if (MusicPlayController.curIndex != -1 && MusicPlayController.curIndex != pagerState.currentPage) {
                lastSheetDiskRotateAngleForSnap = 0f
                sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
                if (abs(MusicPlayController.curIndex - pagerState.currentPage) > 1) {
                    pagerState.scrollToPage(MusicPlayController.curIndex)
                } else {
                    pagerState.animateScrollToPage(MusicPlayController.curIndex, animationSpec = tween(400))
                }
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            if (MusicPlayController.curIndex != pagerState.currentPage) {
                lastSheetDiskRotateAngleForSnap = 0f
                sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
                MusicPlayController.play(pagerState.currentPage)
            }
        }

        HorizontalPager(
            modifier = Modifier
                .padding(top = 208.cdp)
                .fillMaxWidth()
                .height(570.cdp),
            state = pagerState,
        ) { position ->
            DiskItem(MusicPlayController.songList[position])
        }
    }
}

private suspend fun controlSheetNeedleAndDiskAnim() {
    Log.e("ssk", "controlSheetNeedleAndDiskAnim isPlaying=${MusicPlayController.isPlaying()}")
    if (MusicPlayController.isPlaying()) {
        Log.e("ssk", "controlSheetNeedleAndDiskAnim start")
        sheetNeedleUp = false
        sheetDiskRotate.stop()
//        lastSheetDiskRotateAngleForSnap = 0f
        sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
        sheetDiskRotate.animateTo(
            targetValue = 360f + lastSheetDiskRotateAngleForSnap,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = DISK_ROTATE_ANIM_CYCLE, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        Log.e("ssk", "controlSheetNeedleAndDiskAnim end")
    } else {
        sheetNeedleUp = true
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DiskItem(song: SongBean) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Final)
                            if (event.changes.size == 1) {
                                val pointer = event.changes[0]
                                if (pointer.pressed) {
                                    Log.d("ssk", "手指按下")
                                    if (!sheetNeedleUp) {
                                        scope.launch {
                                            lastSheetDiskRotateAngleForSnap = sheetDiskRotate.value
                                            sheetDiskRotate.stop()
                                        }
                                    }
                                    sheetNeedleUp = true

                                } else if (!pointer.pressed) {
                                    Log.d("ssk", "手指抬起")
                                    scope.launch {
                                        delay(200)
                                        if (MusicPlayController.isPlaying()) {
                                            sheetNeedleUp = false
                                            sheetDiskRotate.animateTo(
                                                targetValue = 360f + lastSheetDiskRotateAngleForSnap,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(durationMillis = DISK_ROTATE_ANIM_CYCLE, easing = LinearEasing),
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
                rotationZ = if (song.id == MusicPlayController.songList[MusicPlayController.curIndex].id) sheetDiskRotate.value else 0f
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
            placeholder = R.drawable.ic_defalut_disk_cover,
            error = R.drawable.ic_defalut_disk_cover,
        )
    }
}

@Composable
private fun MiddleActionLayout() {
    Row(
        modifier = Modifier
            .padding(start = 44.cdp, end = 44.cdp, bottom = 32.cdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MiddleActionIcon(R.drawable.ic_like_no)
        MiddleActionIcon(R.drawable.ic_download)
        MiddleActionIcon(R.drawable.ic_action_sing)
        MiddleActionIcon(R.drawable.ic_comment_count)
        MiddleActionIcon(R.drawable.ic_song_more)
    }
}

@Composable
private fun MiddleActionIcon(resId: Int, modifier: Modifier = Modifier, clickable: () -> Unit = {}) {
    CommonIcon(
        resId,
        tint = Color.White,
        modifier = modifier
            .size(78.cdp)
            .clip(CircleShape)
            .clickable {
                clickable.invoke()
            }
            .padding(16.cdp)
    )
}

@Composable
private fun ProgressLayout() {
    Row(
        modifier = Modifier
            .padding(start = 44.cdp, end = 44.cdp, bottom = 32.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = MusicPlayController.curPositionStr, fontSize = 26.csp, color = Color.White)
        SeekBar(
            progress = MusicPlayController.progress,
            seeking = {
                MusicPlayController.seeking(it)
            },
            seekTo = {
                MusicPlayController.seekTo(it)
            },
            modifier = Modifier
                .padding(horizontal = 20.cdp)
                .weight(1f)
        )
        Text(text = MusicPlayController.totalDuringStr, fontSize = 26.csp, color = Color.White)
    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BottomActionLayout() {
    val coroutineScopeScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(start = 20.cdp, end = 20.cdp, bottom = 60.cdp)
            .fillMaxWidth()
            .height(120.cdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(R.drawable.ic_play_serial)
        // 播放上一曲
        ActionButton(R.drawable.ic_action_pre, enable = MusicPlayController.curIndex != 0) {
            //sheetNeedleUp = true
            val newIndex = max(0, MusicPlayController.curIndex - 1)
            Log.e("ssk", "播放上一曲 newIndex=${newIndex}")
            coroutineScopeScope.launch {
                sheetDiskRotate.stop()
                lastSheetDiskRotateAngleForSnap = 0f
                // pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.play(newIndex)
            }
        }
        // 播放or暂停
        ActionButton(if (MusicPlayController.isPlaying()) R.drawable.ic_action_pause else R.drawable.ic_action_play, size = 116) {
            if (MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
                coroutineScopeScope.launch {
                    sheetNeedleUp = true
                    lastSheetDiskRotateAngleForSnap = sheetDiskRotate.value
                    sheetDiskRotate.stop()
                }
            } else {
                MusicPlayController.resume()
            }
        }
        // 播放下一曲
        ActionButton(R.drawable.ic_action_next, enable = MusicPlayController.curIndex != MusicPlayController.songList.size - 1) {
            val newIndex = (MusicPlayController.songList.size - 1).coerceAtMost(MusicPlayController.curIndex + 1)
            Log.e("ssk", "播放下一曲 newIndex=${newIndex}")
            //sheetNeedleUp = true
            coroutineScopeScope.launch {
                sheetDiskRotate.stop()
                lastSheetDiskRotateAngleForSnap = 0f
                //pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.play(newIndex)
            }
        }
        ActionButton(R.drawable.ic_play_list) {
            showPlayListSheet = true
        }
    }
}


@Composable
private fun ActionButton(resId: Int, size: Int = 84, enable: Boolean = true, onClick: () -> Unit = {}) {
    CommonIcon(
        resId,
        tint = if (enable) Color.White else Color(0xFFBBBBBB),
        modifier = Modifier
            .size(size.cdp)
            .clip(CircleShape)
            .onClick(enableRipple = enable) {
                if (enable) {
                    onClick()
                }
            }
            .padding(16.cdp)
    )
}

