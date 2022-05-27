package com.ssk.ncmusic.model

import androidx.annotation.Keep

/**
 * Created by ssk on 2022/4/23.
 */
@Keep
data class PlaylistDetailResult(
    val playlist: PlaylistBean,
) : BaseResult()