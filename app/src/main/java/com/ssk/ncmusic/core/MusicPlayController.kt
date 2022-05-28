package com.ssk.ncmusic.core

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssk.ncmusic.core.player.IPlayerListener
import com.ssk.ncmusic.core.player.NCPlayer
import com.ssk.ncmusic.core.player.PlayMode
import com.ssk.ncmusic.core.player.PlayerStatus
import com.ssk.ncmusic.core.player.event.ChangeSongEvent
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.utils.StringUtil
import com.ssk.ncmusic.utils.showToast
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by ssk on 2022/4/23.
 * 音乐播放控制器，控制CpnBottomMusicPlay、PlayMusicPage的UI状态以及歌曲播放
 */
object MusicPlayController : IPlayerListener {
    // 是否显示底部音乐播放组件
    var showCpnBottomMusicPlay by mutableStateOf(false)
    // 是否显示音乐播放组件
    var showPlayMusicSheet by mutableStateOf(false)

    // 原始歌曲列表
    var originSongList = mutableStateListOf<SongBean>()
    // 当前播放模式下的实际歌曲列表
    var realSongList = mutableStateListOf<SongBean>()
    // 当前播放的歌曲在原始歌曲列表中的索引
    var curOriginIndex by mutableStateOf(-1)
        private set
    // 当前播放的歌曲在当前播放模式下的实际歌曲列表中的索引
    var curRealIndex by mutableStateOf(-1)
        private set
    // 当前播放进度
    var progress by mutableStateOf(0)
    // 当前歌曲播放位置时间文本
    var curPositionStr by mutableStateOf("00:00")
    // 当前歌曲总时长文本
    var totalDuringStr by mutableStateOf("00:00")
    // 是否播放中
    private var playing by mutableStateOf(false)
    // 播放模式
    var playMode by mutableStateOf(PlayMode.LOOP)
        private set
    // 当前播放歌曲总时长
    private var totalDuring = 0
    // 是否拖动进度条中
    private var seeking = false

    init {
        NCPlayer.addListener(this)
    }

    fun setDataSource(songBeans: List<SongBean>, originIndex: Int) {
        originSongList.clear()
        originSongList.addAll(songBeans)
        Log.e("ssk", "MusicPlayController setDataSource curOriginIndex=${originIndex}")
        generateRealSongList(originIndex)
        innerPlay(originSongList[originIndex])
    }

    /**
     * 根据原始歌曲列表索引播放音乐
     */
    fun playByOriginIndex(originIndex: Int) {
        if (originSongList.size > originIndex) {
            curOriginIndex = originIndex
            curRealIndex = realSongList.indexOfFirst { it.id == originSongList[originIndex].id }
            Log.e("ssk", "MusicPlayController playByOriginIndex curOriginIndex=${curOriginIndex}")
            Log.e("ssk", "MusicPlayController playByOriginIndex curPlayModeIndex=${curRealIndex}")
            innerPlay(originSongList[curOriginIndex])
        }
    }

    /**
     * 根据实际播放模式中的歌曲列表索引播放音乐
     */
    fun playByRealIndex(realIndex: Int) {
        if (originSongList.size > realIndex) {
            curRealIndex = realIndex
            curOriginIndex = originSongList.indexOfFirst { it.id == realSongList[realIndex].id }
            Log.e("ssk", "MusicPlayController playByPlayModeIndex curOriginIndex=${curOriginIndex}")
            Log.e("ssk", "MusicPlayController playByPlayModeIndex curPlayModeIndex=${curRealIndex}")
            innerPlay(realSongList[curRealIndex])
        }
    }

    private fun innerPlay(songBean: SongBean) {
        NCPlayer.setDataSource(songBean)
        NCPlayer.start()
        EventBus.getDefault().post(ChangeSongEvent(songBean))
    }

    /**
     * 生成当前播放模式下的歌曲列表
     */
    private fun generateRealSongList(originIndex: Int) {
        when (playMode) {
            PlayMode.RANDOM -> {
                val randomList = mutableListOf<SongBean>()
                randomList.addAll(originSongList)
                randomList.shuffle()
                realSongList.clear()
                realSongList.addAll(randomList)
                val realIndex = realSongList.indexOfFirst { it.id == originSongList[originIndex].id }
                if (realIndex != originIndex) {
                    Collections.swap(realSongList, realIndex, originIndex)
                }
                curOriginIndex = originIndex
                curRealIndex = originIndex
            }
            else -> {
                realSongList.clear()
                realSongList.addAll(originSongList)
                curOriginIndex = originIndex
                curRealIndex = originIndex
            }
        }
        originSongList.forEachIndexed { index, item ->
            Log.e("ssk", "songList $index --> ${item.name}")
        }
        Log.e("ssk", "---------------------------------------")

        realSongList.forEachIndexed { index, item ->
            Log.e("ssk", "pagerSongList $index --> ${item.name}")
        }
    }

    fun pause() {
        NCPlayer.pause()
    }

    fun resume() {
        NCPlayer.resume()
    }

    fun isPlaying(): Boolean {
        return playing
    }

    fun seeking(progress: Int) {
        seeking = true
        this.progress = progress
        if (totalDuring != 0) {
            this.curPositionStr = StringUtil.formatMilliseconds(progress * totalDuring / 100)
        }
    }

    fun seekTo(progress: Int) {
        this.progress = progress
        if (totalDuring != 0) {
            NCPlayer.seekTo(progress * totalDuring / 100)
        }
        seeking = false
    }

    fun isPlaying(songBean: SongBean) = originSongList.getOrNull(curOriginIndex)?.id == songBean.id

    /**
     * 获取当前播放模式下的上一首歌曲索引
     */
    fun getPreRealIndex() = if (curRealIndex == 0) realSongList.size - 1 else curRealIndex - 1

    /**
     * 获取当前播放模式下的下一首歌曲索引
     */
    fun getNextRealIndex() = if (curRealIndex == realSongList.size - 1) 0 else curRealIndex + 1

    fun changePlayMode(playMode: PlayMode) {
        this.playMode = playMode
        generateRealSongList(curOriginIndex)
    }

    override fun onStatusChanged(status: PlayerStatus) {
        playing = status == PlayerStatus.STARTED
        when (status) {
            PlayerStatus.COMPLETED -> {
                autoPlayNext()
            }
            PlayerStatus.ERROR -> {
                showToast("播放失败")
                autoPlayNext()
            }
            PlayerStatus.STOPPED -> {
                totalDuringStr = "00:00"
                curPositionStr = "00:00"
                this.progress = 0
            }
            else -> {}
        }
    }

    private fun autoPlayNext() {
        if(playMode == PlayMode.SINGLE) {
            resume()
        }else {
            val newIndex = getNextRealIndex()
            playByRealIndex(newIndex)
        }
    }

    override fun onProgress(totalDuring: Int, currentPosition: Int, percentage: Int) {
        if (!seeking) {
            this.totalDuring = totalDuring
            totalDuringStr = StringUtil.formatMilliseconds(totalDuring)
            curPositionStr = StringUtil.formatMilliseconds(currentPosition)
            progress = percentage
        }
    }

}