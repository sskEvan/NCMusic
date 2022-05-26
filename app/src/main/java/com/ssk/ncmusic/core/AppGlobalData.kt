package com.ssk.ncmusic.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.utils.kvCacheParcelable

/**
 * Created by ssk on 2022/4/17.
 */
object AppGlobalData {

    var sLoginRefreshFlag by mutableStateOf(false)
    var sLoginResult by kvCacheParcelable(LoginResult::class.java)

}