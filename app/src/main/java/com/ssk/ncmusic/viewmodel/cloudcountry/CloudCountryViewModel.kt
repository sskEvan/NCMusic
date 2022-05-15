package com.ssk.ncmusic.viewmodel.cloudcountry

import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.VideoGroupListResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ssk on 2022/5/14.
 */
@HiltViewModel
class CloudCountryViewModel@Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {
    val videoGroupListResult = ViewStateMutableLiveData<VideoGroupListResult>()

    fun getVideoGroupList() {
        launch(videoGroupListResult) {
            api.getVideoGroupList()
        }
    }
}