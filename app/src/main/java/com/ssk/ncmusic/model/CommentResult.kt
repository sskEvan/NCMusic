package com.ssk.ncmusic.model

/**
 * Created by ssk on 2022/4/28.
 */
data class SongCommentResult(
    val isMusician: Boolean = false,
    val userId: Long = 0,
    val total: Int = 0,
    val more: Boolean = false,
    val comments: List<CommentBean> = emptyList(),
    val topComments: List<CommentBean> = emptyList(),
    val hotComments: List<CommentBean> = emptyList(),
) : BaseResult()

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
    data class Tag(
        val datas: List<TagData>? = null,
    )

    data class TagData(
        val text: String = "",
    )
}

data class FloorComment(
    val replyCount: Long = 0,
    val showReplyCount: Boolean = false,
)


data class CommentUser(
    val nickname: String = "",
    val userId: Long = 0,
    val avatarUrl: String? = null,
)

data class BeReplied(
    val user: CommentUser,
    val content: String? = null,
    val status: Int = 0,
    val beRepliedCommentId: Long = 0,
)

data class NewCommentData(
    val data : CommentData
) : BaseResult()

data class CommentData(
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val comments: List<CommentBean> = emptyList(),
)