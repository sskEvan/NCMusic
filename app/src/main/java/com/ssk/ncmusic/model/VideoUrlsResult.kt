package com.ssk.ncmusic.model

/**
 * Created by ssk on 2022/5/16.
 */
data class VideoUrlsResult(
    val urls: List<VideoUrlBean>
)

data class VideoUrlBean(
    val id: String,
    val url: String,
    val size: Long,
    val validityTime: Int,
    val needPay: Boolean,
    val r: Int
)