package com.ssk.ncmusic.model

import androidx.annotation.Keep

/**
 * Created by ssk on 2022/4/28.
 */
@Keep
data class SongCommentResult(
    val isMusician: Boolean = false,
    val userId: Long = 0,
    val total: Int = 0,
    val more: Boolean = false,
    val comments: List<CommentBean> = emptyList(),
    val topComments: List<CommentBean> = emptyList(),
    val hotComments: List<CommentBean> = emptyList(),
) : BaseResult()

@Keep
data class CommentBean(
    val user: CommentUser,
    val content: String = "",
    val time: Long = 0,
    var likedCount: Int = 0,
    val showFloorComment: FloorComment? = null,
    val tag: Tag? = null,
    val commentId: Long = 0L,
    val beReplied: List<BeReplied>? = null,
    var liked: Boolean = false,
) {
    @Keep
    data class Tag(
        val datas: List<TagData>? = null,
    )

    @Keep
    data class TagData(
        val text: String = "",
    )
}

@Keep
data class FloorComment(
    val replyCount: Long = 0,
    val showReplyCount: Boolean = false,
)

@Keep
data class CommentUser(
    val nickname: String = "",
    val userId: Long = 0,
    val avatarUrl: String? = null,
)

@Keep
data class BeReplied(
    val user: CommentUser,
    val content: String? = null,
    val status: Int = 0,
    val beRepliedCommentId: Long = 0,
)

@Keep
data class NewCommentResult(
    val data : CommentData
) : BaseResult()

@Keep
data class CommentData(
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    var cursor: String,
    val comments: List<CommentBean> = emptyList(),
)

@Keep
data class FloorCommentResult(
    val data : FloorCommentData
) : BaseResult()

@Keep
data class FloorCommentData(
    val totalCount: Int = 0,
    val ownerComment: CommentBean,
    val comments: List<CommentBean> = emptyList(),
)