package com.ssk.ncmusic.viewmodel.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.paging.buildPager
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.CommentBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by ssk on 2022/4/29.
 */
@HiltViewModel
class CommentViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    companion object {
        // 歌曲
        const val TYPE_SONG = 0
        // 视频
        const val TYPE_VIDEO = 5
    }

    val commentSortTabs = listOf(
        CommentSortTab("推荐", 1),
        CommentSortTab("最热", 2),
        CommentSortTab("最新", 3)
    )
    var title by mutableStateOf("评论")
    private var cursors = HashMap<Int, String>()

    fun buildNewCommentListPager(id: String, sortType: Int, type: Int): Flow<PagingData<CommentBean>> {
        FloorCommentViewModel.resourceId = id

        return buildPager(transformListBlock = {
            cursors[sortType] = it?.data?.cursor ?: ""
            val commentCount = it?.data?.totalCount ?: 0
            title = if (commentCount > 0) "评论(${commentCount})" else "评论"
            it?.data?.comments
        }) { curPage, pageSize ->
            api.getNewComment(
                id = id,
                type = type,
                sortType = sortType,
                cursor = cursors[sortType] ?: "",
                pageSize = pageSize,
                pageNo = curPage
            )
        }
    }
}


data class CommentSortTab(var title: String, var type: Int)