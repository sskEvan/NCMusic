package com.ssk.ncmusic.api

import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.model.PlaylistDetailResult
import com.ssk.ncmusic.model.SongDetailResult
import com.ssk.ncmusic.model.UserPlaylistResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by ssk on 2022/4/17.
 */
interface NCApi {

    @GET("login/cellphone")
    suspend fun login(
        @Query("phone") phone: String,
        @Query("password") password: String,
        //@Query("md5_password") passwordMD5: String
    ): LoginResult

    @GET("user/playlist")
    suspend fun getUserPlayList(@Query("uid") uid: String): UserPlaylistResult

    @GET("playlist/detail")
    suspend fun getPlaylistDetail(@Query("id") id: Long): PlaylistDetailResult

    @GET("song/detail")
    suspend fun getSongDetail(@Query("ids") ids: String): SongDetailResult
}