package com.ssk.ncmusic.viewmodel.video

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.customview.widget.ViewDragHelper.STATE_IDLE
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.paging.AppPagingConfig
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.VideoGroupBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by ssk on 2022/5/14.
 */
@HiltViewModel
class VideoPlayViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    var videoFlows: Flow<PagingData<VideoGroupBean>>? = null
    var videoPagingItems: LazyPagingItems<VideoGroupBean>? = null
    var exoPlayer: ExoPlayer? = null


    fun initExoPlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    //   @IntDef({STATE_IDLE, STATE_BUFFERING, STATE_READY, STATE_ENDED})
                    super.onPlaybackStateChanged(playbackState)
                    exoPlayStatus = playbackState
                    Log.e("ssk", "onPlaybackStateChanged playbackState=${playbackState}")
                }
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    Log.e("ssk", "onIsLoadingChanged isLoading=${isLoading}")
                }
            })
        }
    }

    var curVideoUrl by mutableStateOf<String?>(null)
    var exoPlayStatus by mutableStateOf(Player.STATE_IDLE)

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

    fun getVideoUrl(id: String, index: Int) {
        launch(handleResult = {
            videoPagingItems?.itemSnapshotList?.getOrNull(index)?.data?.urls = it.urls
            curVideoUrl = it.urls[0].url
        }) {
            api.getVideoUrl(id)
        }
    }
}