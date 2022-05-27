package com.ssk.ncmusic.ui.page.cloudcountry.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.R
import com.ssk.ncmusic.model.Video
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.page.comment.showVideoCommentSheet
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.StringUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.viewmodel.cloudcountry.VideoPlayViewModel

/**
 * Created by ssk on 2022/5/24.
 */
@Composable
fun BoxScope.CpnVideoInfo(video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .padding(start = 32.cdp, end = 16.cdp)
            .fillMaxWidth()
            .graphicsLayer { alpha = viewModel.videoInfoAlpha }
            .align(Alignment.BottomCenter)
    ) {
        VideoInfoComponent(video)
        BottomComponent()
    }
}

@Composable
private fun VideoInfoComponent(video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    if (viewModel.showVideoInfo) {
        Row(
            //modifier = Modifier.padding(bottom = 40.cdp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CommonNetworkImage(
                        url = video.creator?.avatarUrl,
                        placeholder = R.drawable.ic_default_avator,
                        error = R.drawable.ic_default_avator,
                        modifier = Modifier
                            .size(55.cdp)
                            .clip(
                                RoundedCornerShape(50)
                            )
                    )
                    Text(
                        text = video.creator?.nickname ?: "未知",
                        fontSize = 32.csp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 16.cdp)
                            .weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = video.title ?: "",
                    maxLines = 4,
                    modifier = Modifier.padding(top = 24.cdp),
                    fontSize = 28.csp,
                    color = Color.White,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.cdp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val relateSong = video.relateSong?.getOrNull(0)
                    val songInfo = if (relateSong != null) {
                        "${relateSong.name}-${relateSong.ar.getOrNull(0)?.name ?: "未知"}"
                    } else {
                        "未知-未知"
                    }
                    CommonIcon(
                        resId = R.drawable.ic_music_logo,
                        modifier = Modifier.size(30.cdp),
                        tint = Color.White
                    )
                    Text(
                        text = songInfo,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 24.cdp),
                        fontSize = 28.csp,
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VideoActionButton(
                    R.drawable.ic_video_fabulous,
                    StringUtil.friendlyNumber(video.praisedCount)
                )
                VideoActionButton(
                    R.drawable.ic_video_comment,
                    StringUtil.friendlyNumber(video.commentCount)
                ) {
                    showVideoCommentSheet = true
                }
                VideoActionButton(
                    R.drawable.ic_video_share,
                    StringUtil.friendlyNumber(video.shareCount)
                )
                VideoActionButton(R.drawable.ic_video_collect, "收藏", 10.cdp)

                CpnVideoSongDisk(video)
            }
        }
    }
}

@Composable
private fun VideoActionButton(iconResId: Int, text: String, paddingBottom: Dp = 54.cdp, onClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .padding(bottom = paddingBottom, start = 60.cdp)
            .wrapContentSize()
            .onClick(enableRipple = false) {
                onClick?.invoke()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonIcon(
            resId = iconResId,
            tint = Color.White,
            modifier = Modifier
                .padding(bottom = 12.cdp)
                .size(48.cdp)
        )

        Text(
            text = text,
            color = Color.White,
            fontSize = 28.csp
        )
    }
}

@Composable
private fun BottomComponent() {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(cpnBottomSendCommentHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "千言万语,汇成评论一句话", fontSize = 28.csp, color = AppColorsProvider.current.thirdText,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(54.cdp)
                .onClick {
                    viewModel.showVideoInfo = !viewModel.showVideoInfo
                }
                .clip(RoundedCornerShape(50))
                .background(Color.Gray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CommonIcon(
                resId = if (viewModel.showVideoInfo) R.drawable.ic_video_info_hide else R.drawable.ic_video_info_show,
                modifier = Modifier.size(32.cdp),
                tint = Color.White
            )
        }
    }
}
