package com.ssk.ncmusic.viewmodel.cloudcountry

import androidx.paging.PagingData
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.core.viewstate.paging.AppPagingConfig
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.VideoBean
import com.ssk.ncmusic.model.VideoGroupTabsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by ssk on 2022/5/14.
 */
@HiltViewModel
class CloudCountryViewModel@Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {
    val videoGroupTabsResult = ViewStateMutableLiveData<VideoGroupTabsResult>()
    var videoGroupFlows = HashMap<Int, Flow<PagingData<List<VideoBean>>>>()

    fun getVideoGroupTabs() {
        launch(videoGroupTabsResult) {
            api.getVideoGroupTabs()
        }
    }

    fun buildVideoGroupPager(id: Int) {
        val flow = buildPager(
            config = AppPagingConfig(pageSize = 8, prefetchDistance = 2),
            listSpan = 2,
            transformListBlock = {
                val newList = mutableListOf<List<VideoBean>>()
                it?.datas?.let { originList ->
                    val originListSize = originList.size
                    val columns = 2
                    val newListSize = originListSize / 2
                    for (i in 0 until newListSize)
                        newList.add(originList.subList(i * columns, ((i + 1) * columns).coerceAtMost(originListSize)))
                }
                newList
            }) { curPage, pageSize ->
            api.getVideoGroup(
                id,
                offset = (curPage - 1) * pageSize)
        }
        videoGroupFlows[id] = flow
    }
}