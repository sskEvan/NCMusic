package com.ssk.ncmusic.ui.page.cloudcountry.component

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppConfig
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.model.Video
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.common.SeekBar
import com.ssk.ncmusic.utils.ScreenUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.utils.transformDp
import com.ssk.ncmusic.viewmodel.cloudcountry.VideoPlayViewModel
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/24.
 */
@Composable
fun CpnVideoPlay(index: Int, lazyListState: LazyListState, video: Video) {

    if (video.urls?.getOrNull(0) == null) {
        val viewModel: VideoPlayViewModel = hiltViewModel()
        LaunchedEffect(Unit) {
            viewModel.getVideoUrl(video.vid, index, index != 0)
        }
    }

    // 预加载
    val itemHeight = ScreenUtil.getScreenHeight().transformDp - 1.cdp
    val maxVideoHeight = ScreenUtil.getScreenHeight().transformDp - cpnBottomSendCommentHeight
    val videoWidth = video.width
    val videoHeight = video.height
    val cpnWidth = AppConfig.APP_DESIGN_WIDTH.cdp
    val cpnHeight = ((AppConfig.APP_DESIGN_WIDTH.toFloat() / videoWidth) * videoHeight).cdp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .videoDragDetect(index, lazyListState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxVideoHeight),
            contentAlignment = Alignment.Center
        ) {
            CpnVideoSurface(cpnWidth, cpnHeight, video)
        }

        CpnVideoInfo(video)
        VideoSeekBar()
    }
}

private fun Modifier.videoDragDetect(
    curIndex: Int,
    lazyListState: LazyListState
) = composed {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var totalDragAmount = remember { 0f }
    val threshold = remember { ScreenUtil.getScreenHeight() / 8 }

    this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragStart = {
                totalDragAmount = 0f
            },
            onDragEnd = {
                var newIndex = curIndex
                if (totalDragAmount < 0) {  // 向上滑动
                    if (totalDragAmount < -threshold) {
                        newIndex = curIndex + 1
                        viewModel.switchVideoUrl(newIndex)
                    }
                } else {  //向下滑动
                    if (totalDragAmount > threshold) {
                        newIndex = 0.coerceAtLeast(curIndex - 1)
                        if(newIndex != curIndex) {
                            viewModel.switchVideoUrl(newIndex)
                        }
                    }
                }
                scope.launch {
                    lazyListState.animateScrollToItem(newIndex)
                    viewModel.showVideoInfo = true
                }
            }
        ) { _, dragAmount ->
            // dragAmount 向上滑动为负
            totalDragAmount += dragAmount
            lazyListState.dispatchRawDelta(-dragAmount)
        }
    }
}

@Composable
private fun CpnVideoSurface(cpnWidth: Dp, cpnHeight: Dp, video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()

    if (viewModel.exoPlayStatus == Player.STATE_READY) {
        if (viewModel.curVideoUrl == video.urls?.getOrNull(0)?.url) {
            AndroidView(
                modifier = Modifier
                    .aspectRatio(video.width.toFloat() / video.height)
                    .onClick(enableRipple = false) {
                        if (viewModel.videoPlaying) {
                            viewModel.pauseVideo()
                        } else {
                            viewModel.resumeVideo()
                        }
                    },
                factory = { context ->
                    StyledPlayerView(context).apply {
                        useController = false
                        player = viewModel.exoPlayer
                    }
                })

            if (!viewModel.videoPlaying) {
                CommonIcon(
                    resId = R.drawable.ic_video_play,
                    modifier = Modifier.size(100.cdp),
                    tint = Color.White
                )
            }


        } else {
            CommonNetworkImage(
                url = video.coverUrl,
                modifier = Modifier
                    .width(cpnWidth)
                    .height(cpnHeight),
                placeholder = -1,
                error = -1
            )
        }
    } else {
        CommonNetworkImage(
            url = video.coverUrl,
            modifier = Modifier
                .width(cpnWidth)
                .height(cpnHeight),
            placeholder = -1,
            error = -1
        )
    }
}

@Composable
private fun BoxScope.VideoSeekBar() {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    SeekBar(
        progress = viewModel.videoProgress,
        seeking = {
            viewModel.seeking(it)
        },
        seekTo = {
            viewModel.seekTo(it)
        },
        progressHeight = 2f,
        progressColor = Color.White.copy(0.3f),
        circleColor = Color.LightGray,
        modifier = Modifier
            .padding(bottom = cpnBottomSendCommentHeight)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    )
}


val cpnBottomSendCommentHeight = 100.cdp
