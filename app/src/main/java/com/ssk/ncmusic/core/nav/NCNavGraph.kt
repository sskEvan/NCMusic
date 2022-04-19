package com.ssk.ncmusic.core.nav

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.ssk.ncmusic.ui.page.home.HomePage
import com.ssk.ncmusic.ui.page.login.LoginPage
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
    startDestination: String = Routes.SPLASH,
    onFinish: () -> Unit = { }
) {
    NCNavController.instance = navController

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH) {
            SplashPage()
        }
        composable(Routes.LOGIN) {
            LoginPage()
        }
        composable(Routes.HOME) {
            HomePage()
            BackHandler {
                TwoBackFinish().execute(onFinish)
            }
        }
    }
}