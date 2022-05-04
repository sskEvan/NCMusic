package com.ssk.ncmusic.viewmodel.mine

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.hilt.entrypoint.EntryPointFinder
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.CommentBean
import com.ssk.ncmusic.model.FloorCommentResult
import com.ssk.ncmusic.model.SongBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by ssk on 2022/4/29.
 */
@HiltViewModel
class SongCommentViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {
    val commentSortTabs = listOf(
        CommentSortTab("推荐", 1),
        CommentSortTab("最热", 2),
        CommentSortTab("最新", 3)
    )
    var commentBeanListFlows = HashMap<Int, Flow<PagingData<CommentBean>>>()
    private var cursors = HashMap<Int, String>()

    var showFloorCommentSheet by mutableStateOf(false)
    var songBean: SongBean? = null
    var floorOwnerCommentId by mutableStateOf(0L)
    val floorCommentResult = ViewStateMutableLiveData<FloorCommentResult>()

    fun buildNewCommentListPager(type: Int, songBean: SongBean) {
        Log.e("ssk2", "buildNewCommentListPager done......type=${type}")
        val commentBeanListFlow = buildPager(transformListBlock = {
            cursors[type] = it?.data?.cursor ?: ""
            it?.data?.comments
        }) { curPage, pageSize ->
            api.getNewComment(
                id = songBean.id,
                type = 0,
                sortType = type,
                cursor = cursors[type] ?: "",
                pageSize = pageSize,
                pageNo = curPage
            )
        }
        commentBeanListFlows[type] = commentBeanListFlow
    }

    fun getFloorCommentResult(commentId: Long, songId: Long) {
        launch(floorCommentResult) {
            api.getCommentFloor(commentId, songId)
        }
    }
}


data class CommentSortTab(var title: String, var type: Int)