package com.ssk.ncmusic.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssk.ncmusic.model.SongBean

/**
 * Created by ssk on 2022/4/23.
 */
object MusicPlayController {
    var songList = mutableStateListOf<SongBean>()

    var curIndex by mutableStateOf(0)

    private var playing by mutableStateOf(false)

    fun play() {
        playing = true
    }

    fun pause() {
        playing = false
    }

    fun isPlaying(): Boolean {
        return playing
    }


}