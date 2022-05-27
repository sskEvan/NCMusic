package com.ssk.ncmusic.model

import androidx.annotation.Keep

/**
 * Created by ssk on 2022/5/16.
 */
@Keep
data class VideoUrlsResult(
    val urls: List<VideoUrlBean>
): BaseResult()

@Keep
data class VideoUrlBean(
    val id: String,
    val url: String,
    val size: Long,
    val validityTime: Int,
    val needPay: Boolean,
    val r: Int
)