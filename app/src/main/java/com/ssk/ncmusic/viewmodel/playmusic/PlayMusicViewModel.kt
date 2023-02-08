package com.ssk.ncmusic.viewmodel.playmusic

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssk.ncmusic.core.player.IPlayerListener
import com.ssk.ncmusic.core.player.NCPlayer
import com.ssk.ncmusic.core.player.PlayerStatus
import com.ssk.ncmusic.core.player.event.ChangeSongEvent
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.LyricResult
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.model.SongCommentResult
import com.ssk.ncmusic.utils.LyricUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by ssk on 2022/4/28.
 */
@HiltViewModel
class PlayMusicViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel(), IPlayerListener {

    // disk旋转动画
    val sheetDiskRotate by mutableStateOf(Animatable(0f))

    // 上一次disk旋转角度
    var lastSheetDiskRotateAngleForSnap = 0f

    // 是否抬起磁针
    var sheetNeedleUp by mutableStateOf(true)

    // 是否显示歌词组件
    var showLyric by mutableStateOf(false)

    var songCommentResult by mutableStateOf<SongCommentResult?>(null)

    var lyricResult = ViewStateMutableLiveData<LyricResult>()
    val lyricModelList = mutableListOf<LyricModel>()
    var curLyricIndex by mutableStateOf(-1)
    var curPlayPosition = 0

    init {
        Log.e("ssk", "-------------PlayMusicViewModel init")
        EventBus.getDefault().register(this)
        NCPlayer.addListener(this)
    }

    override fun onCleared() {
        Log.e("ssk", "-------------PlayMusicViewModel onCleared")
        EventBus.getDefault().unregister(this)
        NCPlayer.removeListener(this)
        super.onCleared()
    }

    fun getSongComment(songBean: SongBean) {

        launch(handleSuccessBlock = {
            songCommentResult = it
        }) {
            api.getSongComment(songBean.id, offset = 0)
        }
    }

    fun getLyric(songBean: SongBean) {
        launch(lyricResult, handleSuccessBlock = {
            lyricModelList.clear()
            lyricModelList.addAll(LyricUtil.parse(it))
            curLyricIndex = lyricModelList.indexOfFirst { lyricModel ->
                curPlayPosition < lyricModel.time
            } - 1
        }) {
            curLyricIndex = -1
            api.getLyric(songBean.id)
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onEvent(event: ChangeSongEvent) {
        curLyricIndex = -1
        curPlayPosition = 0
        lyricModelList.clear()
        songCommentResult = null
    }

    override fun onStatusChanged(status: PlayerStatus) {
    }

    override fun onProgress(totalDuring: Int, currentPosition: Int, percentage: Int) {
        Log.d("ssk3", "----------currentPosition=$currentPosition")
        curPlayPosition = currentPosition
        curLyricIndex = lyricModelList.indexOfFirst {
            currentPosition < it.time
        } - 1
        if (currentPosition > lyricModelList.lastOrNull()?.time ?: 0) {
            curLyricIndex = lyricModelList.size - 1
        }
    }
}


data class LyricModel(
    val time: Long,
    val lyric: String? = null,
    var tLyric: String? = null
)