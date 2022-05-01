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
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.hilt.entrypoint.EntryPointFinder
import com.ssk.ncmusic.model.CommentBean
import com.ssk.ncmusic.model.SongBean
import kotlinx.coroutines.flow.Flow

/**
 * Created by ssk on 2022/4/29.
 */
//@HiltViewModel
//class SongCommentViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {
//
//    var commentBeanListFlow: Flow<PagingData<CommentBean>>? = null
//
//    fun buildNewCommentListPager(songBean: SongBean): Flow<PagingData<CommentBean>> {
//        Log.e("ssk", "buildNewCommentListPager done......")
//        commentBeanListFlow = buildPager(transformListBlock = {
//            it?.data?.comments
//        }) { curPage, pageSize ->
//            api.getNewComment(
//                id = songBean.id,
//                type = 0,
//                sortType = 1,
//                pageSize = pageSize,
//                pageNo = curPage
//            )
//        }
//        return commentBeanListFlow!!
//    }
//}

class SongCommentViewModel : BaseViewStateViewModel() {

//    var commentBeanListFlows = mutableStateMapOf<Int, Flow<PagingData<CommentBean>>>()
    var commentBeanListFlows = mutableStateMapOf<Int, Flow<PagingData<CommentBean>>>()
    var cursor = ""
    val commentSortTabs = listOf(
        CommentSortTab("推荐", 1),
        CommentSortTab("最热", 2),
        CommentSortTab("最新", 3)
    )
    var curSelectedTabType by mutableStateOf(1)

    fun buildNewCommentListPager(songBean: SongBean, type: Int) {
        Log.e("ssk", "buildNewCommentListPager done......type=${type}")
        val commentBeanListFlow = buildPager(transformListBlock = {
            cursor = it?.data?.cursor?:""
            it?.data?.comments
        }) { curPage, pageSize ->
            EntryPointFinder.getNCApi().getNewComment(
                id = songBean.id,
                type = 0,
                sortType = type,
                pageSize = pageSize,
                pageNo = curPage,
                cursor = cursor
            )
        }.cachedIn(viewModelScope)
        commentBeanListFlows.clear()
        commentBeanListFlows[type] = commentBeanListFlow
        //return commentBeanListFlow!!
    }
}

data class CommentSortTab(var title: String, var type: Int)