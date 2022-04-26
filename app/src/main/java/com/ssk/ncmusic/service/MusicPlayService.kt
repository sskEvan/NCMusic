package com.ssk.ncmusic.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.ssk.ncmusic.broadcast.MusicNotificationReceiver
import com.ssk.ncmusic.core.NCApplication
import com.ssk.ncmusic.utils.MusicNotificationHelper

/**
 * Created by ssk on 2022/4/26.
 */
class MusicPlayService : Service() {

    private var mReceiver: MusicNotificationReceiver? = null

    companion object {
        const val ACTION_START_MUSIC_SERVICE = "ACTION_START_MUSIC_SERVICE"
        fun start() {
            val intent =  Intent(NCApplication.context, MusicPlayService::class.java).apply {
                action = ACTION_START_MUSIC_SERVICE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NCApplication.context.startForegroundService(intent)
            } else {
                NCApplication.context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            if (it == ACTION_START_MUSIC_SERVICE) {
                MusicNotificationHelper.init {
                    startForeground(MusicNotificationHelper.NOTIFICATION_ID, MusicNotificationHelper.getNotification())
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterBroadcastReceiver()
    }

    override fun onBind(intent: Intent?) = null

    private fun registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = MusicNotificationReceiver()
            registerReceiver(mReceiver, IntentFilter().apply {
                addAction(MusicNotificationReceiver.ACTION_MUSIC_NOTIFICATION)
            })
        }
    }

    private fun unRegisterBroadcastReceiver() {
        mReceiver?.let {
            unregisterReceiver(it)
        }
    }

}