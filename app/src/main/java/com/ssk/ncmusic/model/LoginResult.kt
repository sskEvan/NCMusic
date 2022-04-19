package com.ssk.ncmusic.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by ssk on 2022/4/17.
 */
@Parcelize
class LoginResult(
    val account: AccountBean,
    val profile: ProfileBean,
    val cookie: String
): BaseResult(), Parcelable

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

@Parcelize
data class ProfileBean(
    val detailDescription: String,
    val followed: Boolean,
    val userId: Int,
    val defaultAvatar: Boolean,
    val avatarUrl: String,
    val nickname: String,
    val birthday: Long,
    val avatarImgId: Long,
    val province: Int,
    val accountStatus: Int,
    val vipType: Int,
    val gender: Int,
    val djStatus: Int,
    val avatarImgIdStr: String,
    val backgroundImgIdStr: String,
    val mutual: Boolean,
    val authStatus: Int,
    val backgroundImgId: Long,
    val userType: Int,
    val city: Int,
    val signature: String,
    val authority: Int,
    val description: String,
    val backgroundUrl: String,
    val avatarImgId_str: String,
    val followeds: Int,
    val follows: Int,
    val eventCount: Int,
    val playlistCount: Int,
    val playlistBeSubscribedCount: Int
) : Parcelable
