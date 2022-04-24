package com.ssk.ncmusic.core.player

/**
 * Created by ssk on 2022/4/23.
 */
sealed class PlayerStatus {
    object IDLE: PlayerStatus()
    object PREPARED: PlayerStatus()
    object STARTED: PlayerStatus()
    object PAUSED: PlayerStatus()
    object STOPPED: PlayerStatus()
    object COMPLETED: PlayerStatus()
    object ERROR: PlayerStatus()

}