package com.ssk.ncmusic.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.ssk.ncmusic.service.MusicPlayService
import com.ssk.ncmusic.utils.KVCache
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by ssk on 2022/4/17.
 */
@HiltAndroidApp
class NCApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        KVCache.init(this)
        //MusicPlayService.start()
    }
}