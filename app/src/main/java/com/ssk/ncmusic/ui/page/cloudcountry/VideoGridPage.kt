package com.ssk.ncmusic.ui.page.video

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.ssk.ncmusic.core.viewstate.ViewStateListPagingComponent
import com.ssk.ncmusic.model.VideoBean
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.StringUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.viewmodel.cloudcountry.CloudCountryViewModel

/**
 * Created by ssk on 2022/5/14.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoGridPage(id: Int) {
    val viewModel: CloudCountryViewModel = hiltViewModel()
    if (viewModel.videoGroupFlows[id] == null) {
        viewModel.buildVideoGroupPager(id)
    }
    viewModel.videoGroupFlows[id]?.let {
        val videoGroupItems = it.collectAsLazyPagingItems()

        // ViewStateListPagingComponent设置footer后，加载更多时候有bug，暂时用这种方案实现，todo
        ViewStateListPagingComponent(
            modifier = Modifier
                .padding(horizontal = 24.cdp)
                .fillMaxSize(),
            collectAsLazyPagingItems = videoGroupItems
        ) {
            items(videoGroupItems.itemCount) { outerIndex ->
                Row {
                    videoGroupItems[outerIndex]?.let { items ->
                        items.forEachIndexed { innerIndex, item ->
                            VideoItem(id, outerIndex * 2 + innerIndex, item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.VideoItem(groupId: Int, index: Int, item: VideoBean) {
    Column(
        modifier = Modifier
            .padding(10.cdp)
            .weight(1f)
            .height(550.cdp)
            .clip(RoundedCornerShape(24.cdp))
            .background(AppColorsProvider.current.card)
            .onClick {
                val videoBeanJson = Uri.encode(Gson().toJson(item.data))
                NCNavController.instance.navigate("${RouterUrls.PLAY_VIDEO}/$videoBeanJson/$groupId/$index")
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
                text = item.data.title ?: "",
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
                    url = item.data.creator?.avatarUrl,
                    placeholder = R.drawable.ic_default_avator,
                    error = R.drawable.ic_default_avator,
                    modifier = Modifier
                        .size(40.cdp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                )
                Text(
                    text = item.data.creator?.nickname ?: "未知",
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