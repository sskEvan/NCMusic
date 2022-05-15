package com.ssk.ncmusic.ui.page.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppConfig.APP_DESIGN_WIDTH
import com.ssk.ncmusic.model.VideoBean
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.*
import com.ssk.ncmusic.viewmodel.video.VideoViewModel

private val cpnBottomSendCommentHeight = 100.cdp

/**
 * Created by ssk on 2022/5/15.
 */
@Composable
fun PlayVideoPage() {
//    val viewModel: VideoViewModel = hiltViewModel()
    val json =
        "{\"commentCount\":210,\"coverUrl\":\"https://p2.music.126.net/PZoc3yrzYqDKr4POVs2-5A\\u003d\\u003d/109951163839514725.jpg\",\"creator\":{\"avatarUrl\":\"http://p1.music.126.net/tRbadAo38Mt9KsuVnu5mjg\\u003d\\u003d/109951163019633084.jpg\",\"nickname\":\"小小金鑫\",\"userId\":43442257},\"description\":\"韩语慢摇最喜欢的就是皇冠唱的了，抗韩十八年，死于Tara\",\"durationms\":449051,\"height\":720,\"playTime\":223112,\"praisedCount\":1909,\"previewUrl\":\"http://vodkgeyttp9.vod.126.net/vodkgeyttp8/preview_2297848844_OAgEXv2C.webp?wsSecret\\u003d819a7d01359690737c794eeff1bd1d51\\u0026wsTime\\u003d1652597776\",\"relateSong\":[],\"scm\":\"1.music-video-timeline.video_timeline.video.181017.-295043608\",\"shareCount\":283,\"title\":\"T-ARA Falling U \\u0026 Why We Separated\",\"vid\":\"4AD417F4728BB8F6CBD6702434873D1B\",\"width\":1280}"
    val videoBean = Gson().fromJson(json, VideoBean::class.java)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        CommonTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            backgroundColor = Color.Transparent,
            contentColor = Color.White
        )
        CpnVideo(videoBean)
        CpnVideoInfo(videoBean)
        CpnBottomSendComment()
    }
}

@Composable
private fun CpnVideo(videoBean: VideoBean) {
    val maxVideoHeight = ScreenUtil.getScreenHeight().transformDp - cpnBottomSendCommentHeight

    //val viewModel: VideoViewModel = hiltViewModel()
    //viewModel.curPlayVideoBean.data.let { videoBean ->
    val videoWidth = videoBean.width
    val videoHeight = videoBean.height
    var cpnWidth: Dp
    val cpnHeight: Dp
//        if(videoWidth >= videoHeight) {  //横屏
//            cpnWidth = APP_DESIGN_WIDTH.cdp
//            cpnHeight = ((APP_DESIGN_WIDTH / videoWidth) * videoHeight).cdp
//        }else {  // 竖屏
//            cpnHeight = ScreenUtil.getScreenHeight().transformDp
//            cpnWidth =
//        }
    cpnWidth = APP_DESIGN_WIDTH.cdp
    cpnHeight = ((APP_DESIGN_WIDTH.toFloat() / videoWidth) * videoHeight).cdp
    val videoVerticalPadding = (maxVideoHeight - cpnHeight) / 2

    CommonNetworkImage(
        url = videoBean.coverUrl,
        modifier = Modifier
            .padding(top = videoVerticalPadding)
            .width(cpnWidth)
            .height(cpnHeight),
        placeholder = -1,
        error = -1
    )

    //}
}

@Composable
private fun BoxScope.CpnVideoInfo(videoBean: VideoBean) {
    Row(
        modifier = Modifier
            .padding(bottom = cpnBottomSendCommentHeight)
            .padding(32.cdp)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CommonNetworkImage(
                    url = videoBean.creator.avatarUrl,
                    placeholder = R.drawable.ic_default_avator,
                    error = R.drawable.ic_default_avator,
                    modifier = Modifier
                        .size(55.cdp)
                        .clip(
                            RoundedCornerShape(50)
                        )
                )
                Text(
                    text = videoBean.creator.nickname,
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
                text = videoBean.title,
                maxLines = 4,
                modifier = Modifier.padding(top = 24.cdp),
                fontSize = 28.csp,
                color = Color.White,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CpnVideoActionButton(R.drawable.ic_video_fabulous, StringUtil.friendlyNumber(videoBean.praisedCount))
            CpnVideoActionButton(R.drawable.ic_video_comment, StringUtil.friendlyNumber(videoBean.commentCount))
            CpnVideoActionButton(R.drawable.ic_video_share, StringUtil.friendlyNumber(videoBean.shareCount))
            CpnVideoActionButton(R.drawable.ic_video_collect, "收藏")
        }
    }
}

@Composable
private fun CpnVideoActionButton(iconResId: Int, text: String) {
    Column(
        modifier = Modifier.padding(bottom = 54.cdp),
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
private fun BoxScope.CpnBottomSendComment() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cpnBottomSendCommentHeight)
            .background(Color.Red)
            .align(Alignment.BottomCenter)
    )
}