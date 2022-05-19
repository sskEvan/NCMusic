package com.ssk.ncmusic.model

/**
 * Created by ssk on 2022/5/14.
 */
data class VideoGroupResult(
    val datas: List<VideoGroupBean>
) : BaseResult()

data class VideoGroupBean(
    val type: Int,
    val displayed: Boolean,
    val alg: String?,
    val data: VideoBean
)

data class VideoBean(
    val scm: String?,
    val coverUrl: String?,
    val height: Int,
    val width: Int,
    val title: String,
    val description: String,
    val commentCount: Int,
    val shareCount: Int,
    val creator: Subscribers,
    val previewUrl: String,
    val relateSong: List<SongBean>,
    val vid: String,
    val durationms: Int,
    val playTime: Int,
    val praisedCount: Int,
    var urls: List<VideoUrlBean>? = null
)

