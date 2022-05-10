package com.ssk.ncmusic.viewmodel.mine

import android.content.Context
import android.os.Vibrator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.MockData
import com.ssk.ncmusic.core.NCApplication
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.model.UserPlaylistResult
import com.ssk.ncmusic.ui.common.DragStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * Created by ssk on 2022/4/21.
 */
@HiltViewModel
class MineViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    var favoritePlayList: PlaylistBean? by mutableStateOf(null)
    var selfCreatePlayList: List<PlaylistBean>? by mutableStateOf(null)
    var collectPlayList: List<PlaylistBean>? by mutableStateOf(null)

    val userPlaylistResult = ViewStateMutableLiveData<UserPlaylistResult>()

    var dragStatus by mutableStateOf<DragStatus>(DragStatus.Idle)
    var selectedTabIndex by mutableStateOf(0)

    var selfCreatePlayListHeaderIndex = 0
    var collectPlayListHeaderIndex = 0
    var songHelperIndex = 0

    var vibratorService = NCApplication.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun getUserPlayList() {
        launch(userPlaylistResult, handleResult = {
            val selfCreateList = mutableListOf<PlaylistBean>()
            val collectList = mutableListOf<PlaylistBean>()

            it.playlist.forEach { playListBean ->
                if (playListBean.creator.userId == AppGlobalData.sLoginResult.account.id) {
                    if (playListBean.name == playListBean.creator.nickname + "喜欢的音乐") {
                        favoritePlayList = playListBean
                    } else {
                        selfCreateList.add(playListBean)
                    }
                } else {
                    collectList.add(playListBean)
                }
            }
            selfCreatePlayList = selfCreateList
            collectPlayList = collectList

            selfCreatePlayListHeaderIndex = 0
            collectPlayListHeaderIndex = selfCreatePlayListHeaderIndex + selfCreateList.size + 2
            songHelperIndex = collectPlayListHeaderIndex + collectList.size + 1
        }) {
            //api.getUserPlayList(AppGlobalData.sLoginResult.account.id.toString())

            val gson = Gson()
            gson.fromJson(MockData.playList, UserPlaylistResult::class.java)
        }
    }

    fun vibrator() {
        vibratorService.vibrate(50)
    }
}
