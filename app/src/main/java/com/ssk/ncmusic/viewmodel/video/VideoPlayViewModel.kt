package com.ssk.ncmusic.viewmodel.video

import androidx.paging.PagingData
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
}