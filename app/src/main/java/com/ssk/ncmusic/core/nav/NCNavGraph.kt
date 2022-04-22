package com.ssk.ncmusic.core.nav

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.gson.Gson
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.ui.page.home.HomePage
import com.ssk.ncmusic.ui.page.login.LoginPage
import com.ssk.ncmusic.ui.page.mine.PlaylistPage
import com.ssk.ncmusic.ui.page.profile.ProfilePage
import com.ssk.ncmusic.ui.theme.SplashPage
import com.ssk.ncmusic.utils.TwoBackFinish

/**
 * Created by ssk on 2022/4/17.
 */

object NCNavController {
    @SuppressLint("StaticFieldLeak")
    lateinit var instance: NavHostController
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NCNavGraph(
    navController: NavHostController,
    startDestination: String = RouterUrls.SPLASH,
    onFinish: () -> Unit = { }
) {
    NCNavController.instance = navController

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(RouterUrls.SPLASH) {
            SplashPage()
        }
        composable(RouterUrls.LOGIN) {
            LoginPage()
        }
        composable(RouterUrls.HOME) {
            HomePage()
            BackHandler {
                TwoBackFinish().execute(onFinish)
            }
        }
        composable(RouterUrls.PROFILE,
            enterTransition = { EnterTransition.None }) {
            ProfilePage()
        }
        composable("${RouterUrls.PLAY_LIST}/{${RouterUrls.PLAY_LIST}}") {
            val playlistBeanJson = it.arguments?.getString(RouterUrls.PLAY_LIST)!!
            val playlistBean = Gson().fromJson(playlistBeanJson, PlaylistBean::class.java)
            PlaylistPage(playlistBean)
        }
    }
}
