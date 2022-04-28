package com.ssk.ncmusic.viewmodel.mine

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssk.ncmusic.core.player.event.ChangeSongEvent
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.model.SongCommentResult
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

    var songCommentResult by mutableStateOf<SongCommentResult?>(null)

    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        Log.e("ssk", "-------------PlayMusicViewModel unregister eventbus")
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onEvent(event: ChangeSongEvent) {
        songCommentResult = null
    }

}