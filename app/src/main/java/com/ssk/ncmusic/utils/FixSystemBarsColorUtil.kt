package com.ssk.ncmusic.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.ui.page.home.selectedHomeTabIndex
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.ui.theme.isInDarkTheme

/**
 * Created by ssk on 2022/5/4.
 */
@Composable
fun FixSystemBarsColor() {
    val sysUiController = rememberSystemUiController()

    if (MusicPlayController.showPlayMusicSheet && MusicPlayController.playMusicSheetOffset == 0) {
        sysUiController.setSystemBarsColor(Color.Transparent, false)
    } else {
        val curRouteName = NCNavController.instance.currentBackStackEntryAsState().value?.destination?.route
        if (curRouteName?.split("/")?.get(0) ?: "" == RouterUrls.PLAY_LIST) {  // 歌单详情页，状态栏透明，图标白色
            sysUiController.setSystemBarsColor(Color.Transparent, false)
        } else if (curRouteName == RouterUrls.SPLASH) {  // 闪屏页，状态栏透明，图标白色
            sysUiController.setSystemBarsColor(Color.Transparent, false)
        } else if (curRouteName == RouterUrls.LOGIN) {  // 登录页，状态栏透明，图标白色
            sysUiController.setSystemBarsColor(Color.Transparent, false)
        } else if (curRouteName?.split("/")?.get(0) ?: "" == RouterUrls.SONG_COMMENT) {  // 歌曲评论页，状态栏透明
            sysUiController.setSystemBarsColor(Color.Transparent, !isInDarkTheme())
        } else if (curRouteName == RouterUrls.HOME && selectedHomeTabIndex == 2) {  // 主页面，选中我的tab，状态栏透明
            sysUiController.setSystemBarsColor(Color.Transparent, !isInDarkTheme())
        } else if (curRouteName == RouterUrls.PROFILE) {  // 个人详情页，状态栏透明
            sysUiController.setSystemBarsColor(Color.Transparent, !isInDarkTheme())
        } else if (curRouteName?.split("/")?.get(0) ?: "" == RouterUrls.PLAY_VIDEO) {  // 视频播放页，状态栏透明，图标白色
            sysUiController.setSystemBarsColor(Color.Transparent, false)
        } else {
            sysUiController.setSystemBarsColor(AppColorsProvider.current.statusBarColor, !isInDarkTheme())
        }
    }

    sysUiController.setNavigationBarColor(AppColorsProvider.current.background)
}