package com.ssk.ncmusic.ui.page.mine

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Created by ssk on 2022/4/23.
 */

var showPlayMusicPage by mutableStateOf(false)
var sheetNeedleUp by mutableStateOf(true)
val sheetDiskRotate by mutableStateOf(Animatable(0f))
var lastSheetDiskRotateAngleForSnap = 0f

@Composable
fun PlayMusicPage() {
    if (showPlayMusicPage) {
        PlayMusicSheet()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayMusicSheet() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            Log.e("ssk", "confirmStateChange=${it}")
            scope.launch {
                delay(200)
                showPlayMusicPage = it == ModalBottomSheetValue.Expanded
            }
            true
        }
    )
    LaunchedEffect(showPlayMusicPage) {
        if (showPlayMusicPage) {
            sheetState.show()
        }
    }

    BackHandler(enabled = showPlayMusicPage) {
        scope.launch {
            sheetState.hide()
            showPlayMusicPage = false
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            PlayMusicContent()
        },
        sheetState = sheetState
    ) {

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PlayMusicContent() {
    Log.d("ssk", "-------------222  PlayMusicContent recompose")
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
                            color = AppColorsProvider.current.pure,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = curSong.al.name,
                            fontSize = 24.csp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = AppColorsProvider.current.pure,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.cdp)
                        )
                    }

                },
                leftIconResId = R.drawable.ic_arrow_down,
                backgroundColor = Color.Transparent,
                contentColor = AppColorsProvider.current.pure
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    DiskRoundBackground()
                    DiskPager(pagerState)
                    DiskNeedle()
                }

                BottomActionLayout(pagerState)
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
                transformations(BlurTransformation(LocalContext.current, 10f, 5f))
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
            .padding(top = 100.dp)
            .width(274.dp)
            .height(274.dp)
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
            .padding(start = 70.dp)
            .width(110.dp)
            .height(167.dp)
            .graphicsLayer(
                rotationZ = needleRotateAnim,
                transformOrigin = TransformOrigin(0.164f, 0.109f)
            )
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DiskPager(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        if (MusicPlayController.isPlaying()) {
            sheetNeedleUp = false
            sheetDiskRotate.stop()
            lastSheetDiskRotateAngleForSnap = 0f
            sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
            sheetDiskRotate.animateTo(
                targetValue = 360f + lastSheetDiskRotateAngleForSnap,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    HorizontalPager(
        modifier = Modifier
            .padding(top = 100.dp)
            .fillMaxWidth()
            .height(274.dp),
        state = pagerState,
    ) { position ->
        if (MusicPlayController.curIndex != currentPage) {
            MusicPlayController.play()
            coroutineScope.launch {
                sheetNeedleUp = false
                sheetDiskRotate.stop()
                lastSheetDiskRotateAngleForSnap = 0f
                sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
                MusicPlayController.curIndex = currentPage
                sheetDiskRotate.animateTo(
                    targetValue = 360f + lastSheetDiskRotateAngleForSnap,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 8000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }
        DiskItem(MusicPlayController.songList[position])
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
                                                    animation = tween(durationMillis = 8000, easing = LinearEasing),
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
        Image(
            painter = painterResource(id = R.drawable.ic_disc),
            contentDescription = "disc_background",
            modifier = Modifier
                .width(270.dp)
                .height(270.dp)
        )

        CommonNetworkImage(
            url = song.al.picUrl, modifier = Modifier
                .width(180.dp)
                .height(180.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = CircleShape
                )
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BoxScope.BottomActionLayout(pagerState: PagerState) {
    val coroutineScopeScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 10.dp, vertical = 30.dp)
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(R.drawable.ic_play_serial)
        // 播放上一曲
        ActionButton(R.drawable.ic_action_pre, enable = MusicPlayController.curIndex != 0) {
            sheetNeedleUp = true
            val newIndex = max(0, MusicPlayController.curIndex - 1)
            coroutineScopeScope.launch {
                sheetDiskRotate.stop()
                lastSheetDiskRotateAngleForSnap = 0f
                pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
            }
        }
        // 播放or暂停
        ActionButton(if (MusicPlayController.isPlaying()) R.drawable.ic_action_pause else R.drawable.ic_action_play, size = 56) {
            if (MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
                coroutineScopeScope.launch {
                    sheetNeedleUp = true
                    lastSheetDiskRotateAngleForSnap = sheetDiskRotate.value
                    sheetDiskRotate.stop()
                }
            } else {
                MusicPlayController.play()
                coroutineScopeScope.launch {
                    sheetNeedleUp = false
                    sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
                    sheetDiskRotate.animateTo(
                        targetValue = 360f + lastSheetDiskRotateAngleForSnap,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 8000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }
            }
        }
        // 播放下一曲
        ActionButton(R.drawable.ic_action_next, enable = MusicPlayController.curIndex != MusicPlayController.songList.size - 1) {
            val newIndex = (MusicPlayController.songList.size - 1).coerceAtMost(MusicPlayController.curIndex + 1)
            sheetNeedleUp = true
            coroutineScopeScope.launch {
                sheetDiskRotate.stop()
                lastSheetDiskRotateAngleForSnap = 0f
                pagerState.animateScrollToPage(newIndex, animationSpec = tween(400))
            }
        }
        ActionButton(R.drawable.ic_play_list)
    }
}


@Composable
private fun ActionButton(resId: Int, size: Int = 40, enable: Boolean = true, onClick: () -> Unit = {}) {
    Icon(
        painterResource(resId),
        null,
        tint = if (enable) Color.White else Color.Gray,
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .onClick(enableRipple = enable) {
                if (enable) {
                    onClick()
                }
            }
            .padding(8.dp)
    )
}

