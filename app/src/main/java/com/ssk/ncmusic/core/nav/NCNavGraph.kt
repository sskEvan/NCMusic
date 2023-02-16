package com.ssk.ncmusic.core.nav

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.gson.Gson
import com.ssk.ncmusic.model.PlaylistBean
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.model.Video
import com.ssk.ncmusic.ui.page.TestPage
import com.ssk.ncmusic.ui.page.comment.SongCommentPage
import com.ssk.ncmusic.ui.page.home.HomePage
import com.ssk.ncmusic.ui.page.login.QrcodeLoginPage
import com.ssk.ncmusic.ui.page.mine.PlaylistPage
import com.ssk.ncmusic.ui.page.profile.ProfilePage
import com.ssk.ncmusic.ui.page.splash.SplashPage
import com.ssk.ncmusic.ui.page.video.PlayVideoPage

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
//            TestPage()
        }
        composable(RouterUrls.LOGIN) {
            QrcodeLoginPage()
        }
        composable(RouterUrls.HOME) {
            HomePage { onFinish() }
        }
        composable(RouterUrls.PROFILE,
            enterTransition = { EnterTransition.None }) {
            ProfilePage()
        }
        composable("${RouterUrls.PLAY_LIST}/{${RouterKV.PLAY_LIST_BEAN}}") {
            val playlistBeanJson = it.arguments?.getString(RouterKV.PLAY_LIST_BEAN)!!
            val playlistBean = Gson().fromJson(playlistBeanJson, PlaylistBean::class.java)
            PlaylistPage(playlistBean)
        }
        composable("${RouterUrls.SONG_COMMENT}/{${RouterKV.SONG_BEAN}}") {
            val songBeanJson = it.arguments?.getString(RouterKV.SONG_BEAN)!!
            val songBean = Gson().fromJson(songBeanJson, SongBean::class.java)
            SongCommentPage(songBean)
        }
        composable("${RouterUrls.PLAY_VIDEO}/{${RouterKV.VIDEO_BEAN}}/{${RouterKV.VIDEO_GROUP_ID}}/{${RouterKV.VIDEO_OFFSET_INDEX}}",
            arguments = listOf(
                navArgument(RouterKV.VIDEO_BEAN) { type = NavType.StringType },
                navArgument(RouterKV.VIDEO_GROUP_ID) { type = NavType.IntType },
                navArgument(RouterKV.VIDEO_OFFSET_INDEX) { type = NavType.IntType }
            )) {
            val videoBeanJson = it.arguments?.getString(RouterKV.VIDEO_BEAN)!!
            val videoGroupId = it.arguments?.getInt(RouterKV.VIDEO_GROUP_ID)!!
            val videoOffsetIndex = it.arguments?.getInt(RouterKV.VIDEO_OFFSET_INDEX)!!
            val videoBean = Gson().fromJson(videoBeanJson, Video::class.java)
            PlayVideoPage(videoBean, videoGroupId, videoOffsetIndex)
        }
    }
}

