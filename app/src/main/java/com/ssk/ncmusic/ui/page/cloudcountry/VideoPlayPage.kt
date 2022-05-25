package com.ssk.ncmusic.ui.page.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.core.viewstate.listener.ComposeLifeCycleListener
import com.ssk.ncmusic.model.Video
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.common.LifeCycleObserverComponent
import com.ssk.ncmusic.ui.page.cloudcountry.component.CpnVideoPlay
import com.ssk.ncmusic.ui.page.comment.VideoCommentSheet
import com.ssk.ncmusic.ui.page.comment.showVideoCommentSheet
import com.ssk.ncmusic.viewmodel.cloudcountry.VideoPlayViewModel


/**
 * Created by ssk on 2022/5/15.
 */
@Composable
fun PlayVideoPage(firstVideo: Video, videoGroupId: Int, videoOffsetIndex: Int) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    viewModel.firstVideo = firstVideo
    viewModel.initExoPlayerIfNeeded(LocalContext.current)

    LifeCycleObserverComponent(lifeCycleListener = object : ComposeLifeCycleListener {
        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            viewModel.pauseVideo()
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            viewModel.resumeVideo()
        }
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoList(videoGroupId, videoOffsetIndex)

            if(!showVideoCommentSheet) {
                CommonTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun VideoList(videoGroupId: Int, videoOffsetIndex: Int) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    if (viewModel.videoPagingItems == null) {
        // 获取视频列表
        viewModel.buildVideoPager(videoGroupId, videoOffsetIndex)
    }
    val videoGroupItems = viewModel.videoFlows?.collectAsLazyPagingItems()
    viewModel.videoPagingItems = videoGroupItems

    val lazyListState = rememberLazyListState()

    LaunchedEffect(viewModel.curVideoUrl) {
        if (viewModel.curVideoUrl != null) {
            viewModel.loadVideo(viewModel.curVideoUrl!!)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState
    ) {
        item {
            CpnVideoPlay(0, lazyListState, viewModel.firstVideo)
        }

        videoGroupItems?.let { items ->
            items(items.itemCount) { index ->
                items[index]?.data?.let { video ->
                    CpnVideoPlay(index + 1, lazyListState, video)
                }
            }
        }
    }

    VideoCommentSheet()
}


