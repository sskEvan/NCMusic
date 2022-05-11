package com.ssk.ncmusic.viewmodel.playmusic

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssk.ncmusic.core.player.event.ChangeSongEvent
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
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
class PlayMusicViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    // disk旋转动画
    val sheetDiskRotate by mutableStateOf(Animatable(0f))

    // 上一次disk旋转角度
    var lastSheetDiskRotateAngleForSnap = 0f

    // 是否抬起磁针
    var sheetNeedleUp by mutableStateOf(true)

    // 是否显示歌词组件
    var showLyric by mutableStateOf(false)

    var songCommentResult by mutableStateOf<SongCommentResult?>(null)
    var lyricResult by mutableStateOf<LyricResult?>(null)
    val lyricModelList = mutableListOf<LyricModel>()

    init {
        Log.e("ssk", "-------------PlayMusicViewModel init")
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        Log.e("ssk", "-------------PlayMusicViewModel onCleared")
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    fun getSongComment(songBean: SongBean) {

        launch(handleResult = {
            songCommentResult = it
        }) {
            api.getSongComment(songBean.id, offset = 0)
        }
    }

    fun getLyric(songBean: SongBean) {
        launch(handleResult = {
            lyricResult = it
            lyricModelList.clear()
            lyricModelList.addAll(LyricUtil.parse(it))
            lyricModelList.forEach {
                Log.e("ssk", "getLyric $it")
            }
        }) {
            api.getLyric(songBean.id)
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onEvent(event: ChangeSongEvent) {
        lyricModelList.clear()
        songCommentResult = null
        lyricResult = null
    }

}


data class LyricModel(
    val time: Long,
    val lyric: String? = null,
    var tLyric: String? = null
)