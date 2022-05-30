package com.ssk.ncmusic.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * Created by ssk on 2022/4/17.
 */
@Keep
@Parcelize
class LoginResult(
    val account: AccountBean,
    val profile: ProfileBean,
    val cookie: String
): BaseResult(), Parcelable

@Keep
@Parcelize
data class AccountBean(
    val id: Long,
    val userName: String,
    val type: Int,
    val status: Int,
    val whitelistAuthority: Int,
    val createTime: Long,
    val salt: String,
    val tokenVersion: Int,
    val ban: Int,
    val baoyueVersion: Int,
    val donateVersion: Int,
    val vipType: Int,
    val viptypeVersion: Double,
    val anonimousUser: Boolean
) : Parcelable

@Keep
@Parcelize
data class ProfileBean(
    val followed: Boolean,
    val userId: Int,
    val defaultAvatar: Boolean,
    val avatarUrl: String,
    val nickname: String,
    val birthday: Long,
    val province: Int,
    val accountStatus: Int,
    val vipType: Int,
    val gender: Int,
    val djStatus: Int,
    val mutual: Boolean,
    val authStatus: Int,
    val backgroundImgId: Long,
    val userType: Int,
    val city: Int,
    val followeds: Int,
    val follows: Int,
    val eventCount: Int,
    val playlistCount: Int,
    val playlistBeSubscribedCount: Int
) : Parcelable
