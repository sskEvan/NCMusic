package com.ssk.ncmusic.ui.page.mine.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.ui.page.mine.showPlayMusicPage
import kotlin.math.min
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls

/**
 * Created by ssk on 2022/4/23.
 */

@Composable
fun BoxScope.CpnBottomMusicPlay() {
    if (MusicPlayController.songList.size > 0) {
        val paddingBottom = animateDpAsState(
            targetValue = if (NCNavController.instance.currentBackStackEntryAsState().value?.destination?.route == RouterUrls.HOME) {
                56.dp
            } else {
                0.dp
            }
        )
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingBottom.value),
            visibleState = remember { MutableTransitionState(true) }
                .apply { targetState = !showPlayMusicPage },
            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(200)),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(200))
        ) {
            BottomMusicPlayBar()
        }
    }
}

@Composable
private fun BottomMusicPlayBar() {
    //Log.d("ssk", "-------------BottomMusicPlayBar recompose")
    val diskRotateAngle by remember {
        mutableStateOf(Animatable(0f))
    }

    var lastDiskRotateAngleForSnap by remember { mutableStateOf(0f) }

    LaunchedEffect(MusicPlayController.isPlaying()) {
        if (MusicPlayController.isPlaying()) {
            diskRotateAngle.snapTo(lastDiskRotateAngleForSnap)
            diskRotateAngle.animateTo(
                targetValue = 360f + lastDiskRotateAngleForSnap,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            lastDiskRotateAngleForSnap = diskRotateAngle.value
            diskRotateAngle.stop()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                showPlayMusicPage = true
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.2.dp)
                .background(Color(0xAACCCCCC))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .offset(y = -8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_disc),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Image(
                    painter = rememberImagePainter(MusicPlayController.songList[MusicPlayController.curIndex].al.picUrl,
                        builder = { placeholder(R.drawable.ic_defalut_disk_cover) }),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .rotate(diskRotateAngle.value)
                )
            }

            Text(
                text = MusicPlayController.songList[MusicPlayController.curIndex].name,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 30.dp, bottom = 4.dp)
            )

            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (MusicPlayController.isPlaying()) {
                            MusicPlayController.pause()
                        } else {
                            MusicPlayController.play()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(if (!MusicPlayController.isPlaying()) R.drawable.ic_play_without_circle else R.drawable.ic_pause_without_circle),
                    null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(14.dp)
                )
                CircleProgress(modifier = Modifier.size(28.dp), 33)
            }
        }
    }
}


@Composable
fun CircleProgress(modifier: Modifier = Modifier, progress: Int) {
    val sweepAngle = progress / 100f * 360
    Canvas(modifier = modifier) {
        val canvasSize = min(size.width, size.height)
        drawCircle(color = Color.LightGray, radius = canvasSize / 2, style = Stroke(width = 4f))
        drawArc(color = Color.DarkGray, style = Stroke(width = 4f), startAngle = -90f, sweepAngle = sweepAngle, useCenter = false)
    }
}
