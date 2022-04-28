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
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.utils.StringUtil
import com.ssk.ncmusic.utils.showToast
import java.util.*

/**
 * Created by ssk on 2022/4/23.
 * 音乐播放控制器，控制CpnBottomMusicPlay、PlayMusicPage的UI状态以及歌曲播放
 */
@OptIn(ExperimentalPagerApi::class)
object MusicPlayController : IPlayerListener {

    var songList = mutableStateListOf<SongBean>()
    var playModeSongList = mutableStateListOf<SongBean>()
    var curIndex by mutableStateOf(-1)
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

    fun setDataSource(songBeans: List<SongBean>, index: Int) {
        songList.clear()
        songList.addAll(songBeans)
        Log.e("ssk", "MusicPlayController setDataSource curIndex=${curIndex}")
        generatePlayModeSongList(index)
        NCPlayer.setDataSource(playModeSongList[curIndex])
        NCPlayer.start()
    }

    fun play(index: Int) {
        if (songList.size > index) {
            curIndex = index
            //val playIndex = playModeSongList.indexOfFirst { it.id == songList[index].id }
            Log.e("ssk", "MusicPlayController play curIndex=${curIndex}")
            NCPlayer.setDataSource(playModeSongList[curIndex])
            NCPlayer.start()
        }
    }

    fun getPlayModeIndex(index: Int) = playModeSongList.indexOfFirst { it.id == songList[index].id }

    private fun generatePlayModeSongList(index: Int) {
        when (playMode) {
            PlayMode.RANDOM -> {
                val randomList = mutableListOf<SongBean>()
                randomList.addAll(songList)
                randomList.shuffle()
                playModeSongList.clear()
                playModeSongList.addAll(randomList)
                val newCurIndex = playModeSongList.indexOfFirst { it.id == songList[index].id }
                if (newCurIndex != index) {
                    Collections.swap(playModeSongList, newCurIndex, index)
                }
            }
            else -> {
                playModeSongList.clear()
                playModeSongList.addAll(songList)
            }
        }
        curIndex = index
        songList.forEachIndexed { index, item ->
            Log.e("ssk", "songList $index --> ${item.name}")
        }
        Log.e("ssk", "---------------------------------------")

        playModeSongList.forEachIndexed { index, item ->
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

    fun isPlaying(songBean: SongBean) = playModeSongList.getOrNull(curIndex)?.id == songBean.id

    fun getPreIndex() = if (curIndex == 0) songList.size - 1 else curIndex - 1

    fun getNextIndex() = if (curIndex == songList.size - 1) 0 else curIndex + 1

    fun changePlayMode(playMode: PlayMode) {
        this.playMode = playMode
        generatePlayModeSongList(curIndex)
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
            val newIndex = getNextIndex()
            play(newIndex)
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