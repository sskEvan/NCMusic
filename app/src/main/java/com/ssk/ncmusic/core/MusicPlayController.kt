package com.ssk.ncmusic.core

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.accompanist.pager.ExperimentalPagerApi
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
@OptIn(ExperimentalPagerApi::class)
object MusicPlayController : IPlayerListener {

    var originSongList = mutableStateListOf<SongBean>()
    var realSongList = mutableStateListOf<SongBean>()
    var curOriginIndex by mutableStateOf(-1)
        private set
    var curRealIndex by mutableStateOf(-1)
        private set
    var progress by mutableStateOf(0)
    var curPositionStr by mutableStateOf("00:00")
    var totalDuringStr by mutableStateOf("00:00")
    private var playing by mutableStateOf(false)
    var playMode by mutableStateOf(PlayMode.LOOP)
        private set

    private var totalDuring = 0
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

    //fun getPlayModeIndex(index: Int) = playModeSongList.indexOfFirst { it.id == originSongList[index].id }

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

//    fun getPreIndex() = if (curOriginIndex == 0) originSongList.size - 1 else curOriginIndex - 1
//
//    fun getNextIndex() = if (curOriginIndex == originSongList.size - 1) 0 else curOriginIndex + 1

    fun getPreRealIndex() = if (curRealIndex == 0) realSongList.size - 1 else curRealIndex - 1

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