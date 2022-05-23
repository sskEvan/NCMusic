package com.ssk.ncmusic.ui.page.video

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.ssk.ncmusic.core.AppConfig
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.VideoBean
import com.ssk.ncmusic.ui.common.CommonNetworkImage
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.viewmodel.video.VideoPlayViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ssk on 2022/5/21.
 */

val videoTestModels = listOf(
    VideoTestModel(
        "https://p2.music.126.net/YLx9Rmoghw4XpbMtc2dwDQ==/109951165252175410.jpg",
        "http://vodkgeyttp9c.vod.126.net/cloudmusic/k7JlyjQj_3094828718_shd.mp4?ts=1653147689&rid=47115DC667964F5C42BDE925D7219E80&rl=3&rs=mvMqvdcEhaYjEwJdjafIvoiZPMzyGaVN&sign=2e5af9d077b8501f895c77e38b6f8d1a&ext=NnR5gMvHcZNcbCz592mDGUGuDOFN18isir07K1EOfL1Fc%2FGElFDNAhBKZb2dKIYfGY9Zb81I%2F6o%2F2XHYr726bGaTVuOBYIVKZM%2BoPRY5DyLbBbcafHu8gHTtnowk%2FahPMxi0WwjI0mNkXGac%2BFSCPSGMq4pehDpUiuusQ9VqjtWBiSnkSa0fpXA88e0m6qPKh%2F13ve8GVrmclCyJdGTLVriLH4DJCKYoHVXHi%2Fq9LVB9TBkY36u%2BFY48JOVKxFHB",
        720, 1280
    ),
    VideoTestModel(
        "https://p2.music.126.net/pofWpUD9HJHV4Yjk1qIC-A==/109951163573513088.jpg",
        "http://vodkgeyttp9c.vod.126.net/vodkgeyttp8/8O4Uy9Xu_1584668914_shd.mp4?ts=1653147420&rid=47115DC667964F5C42BDE925D7219E80&rl=3&rs=jrnlTZkjQMjNunYbODiICwddjIsJraUf&sign=3b78865b9a0e26c18e1d16b4fab1c9b6&ext=NnR5gMvHcZNcbCz592mDGUGuDOFN18isir07K1EOfL1Fc%2FGElFDNAhBKZb2dKIYfGY9Zb81I%2F6o%2F2XHYr726bGaTVuOBYIVKZM%2BoPRY5DyLbBbcafHu8gHTtnowk%2FahPMxi0WwjI0mNkXGac%2BFSCPdFUy7vKjBTNrTku2U0MeN6BszIuCd235miQLyVtlqOdYgSbAmEDvkK2flNEY6ErdJvVVbusS5JoH35JXzNIPQj8O32TxW9yIkar6YxnTw2Y",
        720, 1280
    ),
)

@Composable
fun CpnVideoTest() {
    val viewModel: VideoTestViewModel = hiltViewModel()
    viewModel.initExoPlayer(LocalContext.current)
    LaunchedEffect(viewModel.curVideoUrl) {
        if (viewModel.curVideoUrl != null) {
            viewModel.exoPlayer?.let {
                it.stop()
                val playUri = Uri.parse(viewModel.curVideoUrl)
                //构建媒体播放的一个Item， 一个item就是一个播放的多媒体文件
                val item = MediaItem.fromUri(playUri)
                //设置ExoPlayer需要播放的多媒体item
                it.setMediaItem(item)
                //设置播放器是否当装备好就播放， 如果看源码可以看出，ExoPlayer的play()方法也是调用的这个方法
                it.playWhenReady = true
                //资源准备，如果设置 setPlayWhenReady(true) 则资源准备好就立马播放。
                it.prepare();
            }
        }
    }


    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row() {
            Button(onClick = {
                viewModel.index = 0
                viewModel.curVideoUrl = videoTestModels[0].url
            }) {
                Text(text = "第一个")
            }
            Button(onClick = {
                viewModel.index = 1
                viewModel.curVideoUrl = videoTestModels[1].url
            }) {
                Text(text = "第二个")
            }
        }
        CpnVideoSurface(0, videoTestModels[0])
        CpnVideoSurface(0, videoTestModels[1])
    }
}

@Composable
private fun CpnVideoSurface(index: Int, model: VideoTestModel) {
    Box(modifier = Modifier.padding(vertical = 20.cdp)) {


        val viewModel: VideoTestViewModel = hiltViewModel()
        val videoWidth = model.width
        val videoHeight = model.height
        val cpnWidth = AppConfig.APP_DESIGN_WIDTH.cdp
        val cpnHeight = ((AppConfig.APP_DESIGN_WIDTH.toFloat() / videoWidth) * videoHeight).cdp
        if (viewModel.exoPlayStatus == Player.STATE_READY) {
            if (viewModel.curVideoUrl == model.url) {
                AndroidView(
                    modifier = Modifier.aspectRatio(model.width.toFloat() / model.height),
                    factory = { context ->
                        StyledPlayerView(context).apply {
                            useController = false
                            this.player = viewModel.exoPlayer
                        }
                    })

            } else {
                CommonNetworkImage(
                    url = model.cover,
                    modifier = Modifier
                        .width(cpnWidth)
                        .height(cpnHeight),
                    placeholder = -1,
                    error = -1
                )
            }
        } else {
            CommonNetworkImage(
                url = model.cover,
                modifier = Modifier
                    .width(cpnWidth)
                    .height(cpnHeight),
                placeholder = -1,
                error = -1
            )
        }
    }
}


@HiltViewModel
class VideoTestViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {
    var exoPlayer: ExoPlayer? = null
    var exoPlayStatus by mutableStateOf(Player.STATE_IDLE)
    var index by mutableStateOf(0)
    var curVideoUrl by mutableStateOf<String?>(null)

    fun initExoPlayer(context: Context) {
        if(exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        //   @IntDef({STATE_IDLE, STATE_BUFFERING, STATE_READY, STATE_ENDED})
                        super.onPlaybackStateChanged(playbackState)
                        exoPlayStatus = playbackState
                        Log.e("ssk", "onPlaybackStateChanged playbackState=${playbackState}")
                    }

                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        super.onIsLoadingChanged(isLoading)
                        Log.e("ssk", "onIsLoadingChanged isLoading=${isLoading}")
                    }
                })
            }
        }

    }
}

data class VideoTestModel(val cover: String, val url: String, val width: Int, val height: Int)