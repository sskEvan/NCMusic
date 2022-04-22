package com.ssk.ncmusic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by ssk on 2022/4/18.
 */
/**
 * 歌单详情的bean，里面包含了歌单的歌曲
 */
data class PlaylistDetailBean(
    val playlist: PlaylistBean,
) : BaseResult()

/**
 * 个人歌单
 */
data class UserPlaylistResult(
    val playlist: List<PlaylistBean>,
) : BaseResult()

@Parcelize
data class PlaylistBean(
    val tracks: List<Track>?,
    val trackIds: List<TrackId>?,
    val creator: Subscribers,
    val name: String = "",
    val coverImgUrl: String = "",
    val trackCount: Int = 0,
    val id: Long = 0,
    val playCount: Long = 0,
    val description: String?,
    val shareCount: Int,
    val commentCount: Int
) : Parcelable

data class Subscribers(
    val userId: Long,
    val avatarUrl: String,
    val nickname: String
): Serializable

data class Track(
    val name: String,
    val id: Long,
    val mv: Long,
    val ar: List<Ar>,
    val al: Al,
): Serializable

data class TrackId(
    val id: Int = 0,
    val v: Int = 0,
    val alg: String? = null
): Serializable

data class Ar(
    val id: Long,
    val name: String,
)

data class Al(
    val id: Long,
    val name: String,
    val picUrl: String,
)