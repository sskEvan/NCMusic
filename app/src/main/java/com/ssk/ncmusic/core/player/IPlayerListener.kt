package com.ssk.ncmusic.core.player

import com.ssk.ncmusic.model.SongBean

/**
 * Created by ssk on 2022/4/23.
 */
interface IPlayerListener {
    fun onStatusChanged(status: PlayerStatus)
    fun onProgress(totalDuring: Int, currentPosition: Int, percentage: Int)
}