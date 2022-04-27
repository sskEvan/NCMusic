package com.ssk.ncmusic.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.MockData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.utils.cdp
import kotlinx.coroutines.delay

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun SplashPage() {

    val sysUiController = rememberSystemUiController()
    sysUiController.setSystemBarsColor(
        Color.Transparent,
        darkIcons = false,
    )

    LaunchedEffect(Unit) {
        delay(1000)
        NCNavController.instance.popBackStack()
//        val loginResult = AppGlobalData.sLoginResult
//        loginResult?.let {
//            val json = Gson().toJson(loginResult)
//            Log.e("ssk", "json=${json}")
//        }
        if(AppGlobalData.sLoginResult == null) {
            val newLoginResult = Gson().fromJson(MockData.loginResult, LoginResult::class.java)
            AppGlobalData.sLoginResult = newLoginResult
        }
        NCNavController.instance.navigate(if (AppGlobalData.sLoginResult == null) RouterUrls.LOGIN else RouterUrls.HOME)
        //NCNavController.instance.navigate(RouterUrls.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.primary),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            Modifier
                .padding(top = 485.cdp)
                .size(190.cdp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 480.cdp)
                .size(200.cdp)
                .clip(RoundedCornerShape(50)),
            tint = AppColorsProvider.current.primaryVariant
        )
    }

}