package com.ssk.ncmusic.viewmodel.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.CommentBean
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
    var title by mutableStateOf("评论")
    private var cursors = HashMap<Int, String>()

    var showFloorCommentSheet by mutableStateOf(false)
    var songBean: SongBean? = null
    var floorOwnerCommentId by mutableStateOf(0L)
    var floorOwnerCommentBean: CommentBean? = null
    var floorCommentBeanListFlow by mutableStateOf<Flow<PagingData<CommentBean>>?>(null)
    var floorPagingTime = 0L
    var floorCommentTitle by mutableStateOf("回复")

    fun buildNewCommentListPager(type: Int, songBean: SongBean) {
        val commentBeanListFlow = buildPager(transformListBlock = {
            cursors[type] = it?.data?.cursor ?: ""
            val commentCount = it?.data?.totalCount ?: 0
            title = if (commentCount > 0) "评论(${commentCount})" else "评论"
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
        floorCommentBeanListFlow = null
        floorPagingTime = 0
        floorCommentTitle = "回复"

        floorCommentBeanListFlow = buildPager(transformListBlock = {
            floorPagingTime = it?.data?.comments?.lastOrNull()?.time ?: 0L
            it?.data?.ownerComment?.let {
                floorOwnerCommentBean = it
            }
            if (it?.data?.totalCount ?: 0 > 0) {
                val commentCount = it?.data?.totalCount ?: 0
                floorCommentTitle = if (commentCount > 0) "回复(${commentCount})" else "回复"
            }
            it?.data?.comments
        }) { _, pageSize ->
            api.getCommentFloor(
                parentCommentId = commentId,
                id = songId,
                type = 0,
                time = floorPagingTime,
                limit = pageSize,
            )
        }
    }
}


data class CommentSortTab(var title: String, var type: Int)