package com.ssk.ncmusic.viewmodel.cloudcountry

import android.util.Log
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
class CloudCountryViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {
    val videoGroupTabsResult = ViewStateMutableLiveData<VideoGroupTabsResult>()

    fun getVideoGroupTabs() {
        launch(videoGroupTabsResult) {
            api.getVideoGroupTabs()
        }
    }

    fun buildVideoGroupPager(id: Int) : Flow<PagingData<VideoBean>> {
        Log.e("ssk", "buildVideoGroupPager done id=${id}")
       return buildPager(
            config = AppPagingConfig(pageSize = 8, prefetchDistance = 2),
            transformListBlock = {
                it?.datas
            }) { curPage, pageSize ->
            api.getVideoGroup(
                id,
                offset = (curPage - 1) * pageSize
            )
        }
    }
}