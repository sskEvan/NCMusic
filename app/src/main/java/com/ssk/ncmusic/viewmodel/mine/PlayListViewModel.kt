package com.ssk.ncmusic.viewmodel.mine

import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.model.SongDetailResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ssk on 2022/4/23.
 */
@HiltViewModel
class PlayListViewModel @Inject constructor(private val api : NCApi) : BaseViewStateViewModel() {

    lateinit var playlistBean: PlaylistBean

    val songDetailResult = ViewStateMutableLiveData<SongDetailResult>()
    val songList = mutableListOf<SongBean>()

    fun getSongDetail() {
        launch(songDetailResult, handleSuccessBlock = {
            songList.addAll(it.songs)
        }) {
            //val trackIdBeans = playlistBean.trackIds
            val playlistDetailResult = api.getPlaylistDetail(playlistBean.id)
            val trackIdBeans = playlistDetailResult.playlist.trackIds
            val ids = StringBuilder()
            if(trackIdBeans != null) {
                val size = trackIdBeans.size
                for (i in 0 until size) {
                    //最后一个参数不加逗号
                    if (i == size - 1) {
                        ids.append(trackIdBeans[i].id)
                    } else {
                        ids.append(trackIdBeans[i].id).append(",")
                    }
                }
            }
            api.getSongDetail(ids.toString())
        }
    }

}