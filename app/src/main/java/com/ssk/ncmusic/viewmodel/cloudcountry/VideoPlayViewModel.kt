package com.ssk.ncmusic.viewmodel.cloudcountry

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.paging.AppPagingConfig
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.Video
import com.ssk.ncmusic.model.VideoBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

/**
 * Created by ssk on 2022/5/14.
 */
@HiltViewModel
class VideoPlayViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    var videoFlows: Flow<PagingData<VideoBean>>? = null
    var videoPagingItems: LazyPagingItems<VideoBean>? = null
    var curVideoUrl by mutableStateOf<String?>(null)
    var exoPlayStatus by mutableStateOf(Player.STATE_IDLE)
    var videoPlaying by mutableStateOf(false)
    var showVideoInfo by mutableStateOf(true)
    var videoProgress by mutableStateOf(0)
    lateinit var firstVideo: Video
    lateinit var exoPlayer: ExoPlayer
    // 是否拖动进度条中
    private var seeking = false

    private val mTimer: Timer = Timer()
    private var mUpdateDuringTask: TimerTask? = null
    private var mHandler = Handler(Looper.getMainLooper())
    private var exoPlayListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            //@IntDef({STATE_IDLE, STATE_BUFFERING, STATE_READY, STATE_ENDED})
            super.onPlaybackStateChanged(playbackState)
            exoPlayStatus = playbackState
            if(exoPlayStatus == STATE_READY && MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            videoPlaying = isPlaying
            if (isPlaying) {
                startUpdateDuringTask()
            }else {
                mUpdateDuringTask?.cancel()
            }
        }

    }

    fun seeking(progress: Int) {
        seeking = true
        videoProgress = progress
    }

    fun seekTo(progress: Int) {
        seeking = false
        val curPosition = exoPlayer.duration * progress / 100
        exoPlayer.seekTo(curPosition)
    }

    /**
     * 初始化exo播放器
     */
    fun initExoPlayerIfNeeded(context: Context) {
        if (!::exoPlayer.isInitialized) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(exoPlayListener)

            }
        }
    }

    /**
     * 暂停视频播放
     */
    fun pauseVideo() {
        curVideoUrl?.run {
            exoPlayer.pause()
            mUpdateDuringTask?.cancel()
        }
    }

    /**
     * 恢复视频播放
     */
    fun resumeVideo() {
        curVideoUrl?.run {
            exoPlayer.play()
            startUpdateDuringTask()
        }
    }

    /**
     * 停止视频播放
     */
    private fun stopVideo() {
        videoProgress = 0
        curVideoUrl?.run {
            exoPlayer.stop()
            mUpdateDuringTask?.cancel()
        }
    }

    /**
     * 释放
     */
    fun release() {
        exoPlayer.removeListener(exoPlayListener)
        exoPlayer.release()
    }

    /**
     * 加载视频
     */
    fun loadVideo(url: String){
        val playUri = Uri.parse(url)
        //构建媒体播放的一个Item， 一个item就是一个播放的多媒体文件
        val item = MediaItem.fromUri(playUri)
        //设置ExoPlayer需要播放的多媒体item
        exoPlayer.setMediaItem(item)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        //设置播放器是否当装备好就播放， 如果看源码可以看出，ExoPlayer的play()方法也是调用的这个方法
        exoPlayer.playWhenReady = true
        //资源准备，如果设置 setPlayWhenReady(true) 则资源准备好就立马播放。
        exoPlayer.prepare()
    }

    /**
     * 切换视频播放地址
     */
    fun switchVideoUrl(newIndex: Int) {
        stopVideo()
        val curVideo = if (newIndex > 0) {
            videoPagingItems?.itemSnapshotList?.getOrNull(newIndex - 1)?.data
        } else {
            firstVideo
        }
        val videoUrlBean = curVideo?.urls?.getOrNull(0)
        if (videoUrlBean == null && curVideo != null) {
            // 获取视频播放地址
            getVideoUrl(curVideo.vid, newIndex, false)
        } else {
            videoUrlBean?.let {
                // 由curVideoUrl来驱动视频播放
                curVideoUrl = it.url
            }
        }
    }

    fun buildVideoPager(id: Int, initOffset: Int) {
        videoFlows = buildPager(
            config = AppPagingConfig(pageSize = 8),
            transformListBlock = {
                it?.datas
            }) { curPage, pageSize ->
            api.getVideoGroup(
                id,
                offset = initOffset + (curPage - 1) * pageSize + 1,
            )
        }
    }

    fun getVideoUrl(id: String, index: Int, isPreLoad: Boolean = false) {
        launch(handleResult = {
            if (index == 0) {
                firstVideo.urls = it.urls
            } else {
                videoPagingItems?.itemSnapshotList?.getOrNull(index - 1)?.data?.urls = it.urls
            }
            if(!isPreLoad) {
                // 由curVideoUrl来驱动视频播放
                curVideoUrl = it.urls[0].url
            }
            Log.e("ssk5", "getVideoUrl done index=${index}")
        }) {
            api.getVideoUrl(id)
        }
    }

    override fun onCleared() {
        mUpdateDuringTask?.cancel()
        release()
        super.onCleared()
    }

    private fun startUpdateDuringTask() {
        mUpdateDuringTask?.cancel()
        mUpdateDuringTask = object : TimerTask() {
            override fun run() {
                if(!seeking) {
                    mHandler.post {
                        videoProgress = (exoPlayer.currentPosition.toFloat() * 100 / exoPlayer.duration).toInt()
                    }
                }
            }
        }.apply {
            mTimer.schedule(this, 0, 1000)
        }
    }

}