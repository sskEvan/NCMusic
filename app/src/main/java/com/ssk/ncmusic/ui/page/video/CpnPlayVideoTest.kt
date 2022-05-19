package com.ssk.ncmusic.ui.page.video

import android.R
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView

val url =
    "http://vodkgeyttp9c.vod.126.net/vodkgeyttp8/68sKFbGS_1328070069_hd.mp4?ts=1652881609&rid=47115DC667964F5C42BDE925D7219E80&rl=3&rs=pERMSThVhQSaBeeJtFaSsjitlarhQRsf&sign=b696d5e48fe0eeb6f10d2fa0711ffdaa&ext=NnR5gMvHcZNcbCz592mDGUGuDOFN18isir07K1EOfL1Fc%2FGElFDNAhBKZb2dKIYfGY9Zb81I%2F6o%2F2XHYr726bGaTVuOBYIVKZM%2BoPRY5DyLbBbcafHu8gHTtnowk%2FahP77y92d%2BwfBYvfNK8daQ7JVuHiCNcQWWDhlRZuuES%2BjhyMub7tApjHKg7JFX9S7ebpWDKjDChjGvM2sP9l55p1VlRDs3CxoCSGpYBHumU23RF6k%2FPnc4DYff1p%2FrrNq92"

@Composable
fun CpnPlayVideoTest() {

    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    //   @IntDef({STATE_IDLE, STATE_BUFFERING, STATE_READY, STATE_ENDED})
                    super.onPlaybackStateChanged(playbackState)
                    Log.e("ssk", "onPlaybackStateChanged playbackState=${playbackState}")
                }
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    Log.e("ssk", "onIsLoadingChanged isLoading=${isLoading}")
                }
            })
        }
    };

    LaunchedEffect(Unit) {
        val playUri = Uri.parse(url)
//全部使用默认设置初始化ExoPlayer

        //构建媒体播放的一个Item， 一个item就是一个播放的多媒体文件
        val item = MediaItem.fromUri(playUri)
        //设置ExoPlayer需要播放的多媒体item
        player.setMediaItem(item)
        //设置播放器是否当装备好就播放， 如果看源码可以看出，ExoPlayer的play()方法也是调用的这个方法
        player.setPlayWhenReady(true)
        //资源准备，如果设置 setPlayWhenReady(true) 则资源准备好就立马播放。
        player.prepare();
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(

            modifier = Modifier.aspectRatio(1.77f),
            factory = { context ->
//                val frameLayout = FrameLayout(context)
//                frameLayout.setBackgroundColor(Color.parseColor("#000000"))
//                frameLayout
                StyledPlayerView(context).apply {
                    useController = false
                    this.player = player
                }
            })


    }
}