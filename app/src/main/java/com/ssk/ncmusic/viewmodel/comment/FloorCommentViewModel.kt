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
 * Created by ssk on 2023/2/16.
 */
@HiltViewModel
class FloorCommentViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    companion object {
        var showFloorCommentSheet by mutableStateOf(false)
        var floorOwnerCommentId = 0L
        var resourceId = ""
    }

    var floorOwnerCommentBean: CommentBean? = null
    private var floorPagingTime = 0L
    var floorCommentTitle by mutableStateOf("回复")

    fun getFloorCommentResult(type: Int) : Flow<PagingData<CommentBean>> {
        floorPagingTime = 0
        floorCommentTitle = "回复"

        return buildPager(transformListBlock = {
            floorPagingTime = it?.data?.comments?.lastOrNull()?.time ?: 0L
            it?.data?.ownerComment?.let { commentBean ->
                floorOwnerCommentBean = commentBean
            }
            if ((it?.data?.totalCount ?: 0) > 0) {
                val commentCount = it?.data?.totalCount ?: 0
                floorCommentTitle = if (commentCount > 0) "回复(${commentCount})" else "回复"
            }
            it?.data?.comments
        }) { _, pageSize ->
            api.getCommentFloor(
                parentCommentId = floorOwnerCommentId,
                id = resourceId,
                type = type,
                time = floorPagingTime,
                limit = pageSize,
            )
        }
    }
}