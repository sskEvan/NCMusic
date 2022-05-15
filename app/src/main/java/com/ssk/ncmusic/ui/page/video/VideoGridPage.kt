package com.ssk.ncmusic.ui.page.video

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.viewstate.ViewStateGridPagingComponent
import com.ssk.ncmusic.model.VideoGroupBean
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.StringUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.viewmodel.video.VideoViewModel

/**
 * Created by ssk on 2022/5/14.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoGridPage(id: Int) {
    val viewModel: VideoViewModel = hiltViewModel()
    if (viewModel.videoGroupFlows[id] == null) {
        viewModel.buildVideoGroupPager(id)
    }
    viewModel.videoGroupFlows[id]?.let {
        val videoGroupItems = it.collectAsLazyPagingItems()

        ViewStateGridPagingComponent(
            modifier = Modifier
                .padding(horizontal = 24.cdp)
                .fillMaxSize(),
            columns = 2,
            collectAsLazyPagingItems = videoGroupItems
        ) {
            Log.e("ssk3", "ViewStateGridPagingComponent inner recompose.......videoGroupItems.itemCount=${videoGroupItems.itemCount}")
            items(videoGroupItems.itemCount) { index ->
                videoGroupItems[index]?.let { item ->
                    VideoItem(item, viewModel)
                }
            }
        }
    }
}

@Composable
private fun VideoItem(item: VideoGroupBean, viewModel: VideoViewModel) {
    Column(
        modifier = Modifier
            .padding(10.cdp)
            .fillMaxWidth()
            .height(550.cdp)
            .clip(RoundedCornerShape(24.cdp))
            .background(AppColorsProvider.current.card)
            .onClick {
                viewModel.curPlayVideoBean = item
                val json = Gson().toJson(item.data)
                NCNavController.instance.navigate(RouterUrls.PLAY_VIDEO)
            }
    ) {
        CommonNetworkImage(
            url = item.data.coverUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.cdp),
            placeholder = R.drawable.ic_default_placeholder_video,
            error = R.drawable.ic_default_placeholder_video
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.cdp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(
                text = item.data.title,
                color = AppColorsProvider.current.firstText,
                fontSize = 28.csp,
                maxLines = 2,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.cdp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CommonNetworkImage(
                    url = item.data.creator.avatarUrl,
                    placeholder = R.drawable.ic_default_avator,
                    error = R.drawable.ic_default_avator,
                    modifier = Modifier
                        .size(40.cdp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                )
                Text(
                    text = item.data.creator.nickname,
                    fontSize = 24.csp,
                    color = AppColorsProvider.current.secondText,
                    modifier = Modifier
                        .padding(start = 10.cdp)
                        .weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CommonIcon(
                        resId = R.drawable.ic_fabulous, modifier = Modifier.size(28.cdp),
                        tint = AppColorsProvider.current.secondIcon
                    )

                    Text(
                        text = StringUtil.friendlyNumber(item.data.praisedCount),
                        fontSize = 24.csp,
                        color = AppColorsProvider.current.secondText,
                        modifier = Modifier
                            .padding(start = 10.cdp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}