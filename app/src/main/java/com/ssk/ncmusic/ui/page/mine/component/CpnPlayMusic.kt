package com.ssk.ncmusic.ui.page.mine.component

import android.annotation.SuppressLint
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.player.PlayMode
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.*
import com.ssk.ncmusic.ui.page.mine.DISK_ROTATE_ANIM_CYCLE
import com.ssk.ncmusic.ui.page.showPlayListSheet
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.mine.PlayMusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Created by ssk on 2022/4/25.
 */


@OptIn(ExperimentalPagerApi::class)
@Composable
fun CpnPlayMusic(backCallback: () -> Unit) {
    Log.d("ssk", "PlayMusicContent recompose")
    DisposableEffect(Unit) {
        onDispose {
            Log.e("ssk", "!!!!!!!!!!!!!!!!!CpnPlayMusic onDispose")
        }
    }
    val pagerState = rememberPagerState(
        initialPage = MusicPlayController.curRealIndex,
        pageCount = MusicPlayController.realSongList.size,
        infiniteLoop = true
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
        val curSong = MusicPlayController.realSongList[MusicPlayController.curRealIndex]
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

@OptIn(ExperimentalCoilApi::class)
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
private fun DiskPager(pagerState: PagerState) {
    Log.d("ssk", "DiskPager recompose")
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
                    pagerState.animateScrollToPage(MusicPlayController.curRealIndex, animationSpec = tween(400))
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
//        lastSheetDiskRotateAngleForSnap = 0f
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
                                    if (!viewModel.sheetNeedleUp) {
                                        scope.launch {
                                            viewModel.lastSheetDiskRotateAngleForSnap = viewModel.sheetDiskRotate.value
                                            viewModel.sheetDiskRotate.stop()
                                        }
                                    }
                                    viewModel.sheetNeedleUp = true

                                } else if (!pointer.pressed) {
                                    scope.launch {
                                        delay(400)
                                        if (MusicPlayController.isPlaying()) {
                                            viewModel.sheetNeedleUp = false
                                            viewModel.sheetDiskRotate.animateTo(
                                                targetValue = 360f + viewModel.lastSheetDiskRotateAngleForSnap,
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

@Composable
private fun MiddleActionLayout() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    LaunchedEffect(MusicPlayController.curRealIndex) {
        viewModel.getSongComment(MusicPlayController.realSongList[MusicPlayController.curRealIndex])
    }
    Row(
        modifier = Modifier
            .padding(start = 60.cdp, end = 60.cdp, bottom = 32.cdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MiddleActionIcon(R.drawable.ic_like_no, modifier = Modifier.padding(end = 60.cdp))
        MiddleActionIcon(R.drawable.ic_download, modifier = Modifier.padding(end = 60.cdp))
        MiddleActionIcon(R.drawable.ic_action_sing, modifier = Modifier.padding(end = 60.cdp))
        Box(modifier = Modifier.width(138.cdp)) {
            MiddleActionIcon(R.drawable.ic_comment_count) {
                val json = Uri.encode(Gson().toJson(MusicPlayController.realSongList[MusicPlayController.curRealIndex]))
                NCNavController.instance.navigate("${RouterUrls.SONG_COMMENT}/$json")
                scope.launch {
                    delay(300)
                    MusicPlayController.playMusicSheetOffset = ScreenUtil.getScreenHeight()
                }
            }
            viewModel.songCommentResult?.let {
                val commentText = StringUtil.friendlyNumber(it.total)
                Text(
                    text = commentText,
                    color = Color.White,
                    fontSize = 18.csp,
                    modifier = Modifier
                        .padding(top = 10.cdp, start = 52.cdp)
                        .align(Alignment.TopStart)
                )
            }
        }
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
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val coroutineScopeScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(start = 20.cdp, end = 20.cdp, bottom = 60.cdp)
            .fillMaxWidth()
            .height(120.cdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val playModeResId = when (MusicPlayController.playMode) {
            PlayMode.RANDOM -> R.drawable.ic_play_mode_random
            PlayMode.SINGLE -> R.drawable.ic_play_mode_single
            PlayMode.LOOP -> R.drawable.ic_play_mode_loop
        }
        ActionButton(playModeResId) {
            when (MusicPlayController.playMode) {
                PlayMode.RANDOM -> MusicPlayController.changePlayMode(PlayMode.SINGLE)
                PlayMode.SINGLE -> MusicPlayController.changePlayMode(PlayMode.LOOP)
                PlayMode.LOOP -> MusicPlayController.changePlayMode(PlayMode.RANDOM)
            }
        }
        // 播放上一曲
        ActionButton(R.drawable.ic_action_pre) {
            //sheetNeedleUp = true
            val newIndex = MusicPlayController.getPreRealIndex()
            Log.e("ssk", "播放上一曲 newIndex=${newIndex}")
            coroutineScopeScope.launch {
                viewModel.sheetDiskRotate.stop()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                // pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.playByRealIndex(newIndex)
            }
        }
        // 播放or暂停
        ActionButton(if (MusicPlayController.isPlaying()) R.drawable.ic_action_pause else R.drawable.ic_action_play, size = 116) {
            if (MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
                coroutineScopeScope.launch {
                    viewModel.sheetNeedleUp = true
                    viewModel.lastSheetDiskRotateAngleForSnap = viewModel.sheetDiskRotate.value
                    viewModel.sheetDiskRotate.stop()
                }
            } else {
                MusicPlayController.resume()
            }
        }
        // 播放下一曲
        ActionButton(R.drawable.ic_action_next) {
            val newIndex = MusicPlayController.getNextRealIndex()
            Log.e("ssk", "播放下一曲 newIndex=${newIndex}")
            //sheetNeedleUp = true
            coroutineScopeScope.launch {
                viewModel.sheetDiskRotate.stop()
                viewModel.lastSheetDiskRotateAngleForSnap = 0f
                //pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
                MusicPlayController.playByRealIndex(newIndex)
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

