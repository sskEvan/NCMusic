package com.ssk.ncmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ssk.ncmusic.core.nav.NCNavGraph
import com.ssk.ncmusic.ui.page.playmusic.PlayListSheet
import com.ssk.ncmusic.ui.page.playmusic.PlayMusicSheet
import com.ssk.ncmusic.ui.page.playmusic.component.CpnBottomPlayMusic
import com.ssk.ncmusic.ui.theme.AppTheme
import com.ssk.ncmusic.ui.theme.themeTypeState
import com.ssk.ncmusic.utils.FixSystemBarsColor
import com.ssk.ncmusic.utils.setAndroidNativeLightStatusBar
import com.ssk.ncmusic.utils.transformDp
import com.ssk.ncmusic.utils.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setAndroidNativeLightStatusBar()
        setContent {
            AppTheme(themeTypeState.value) {
                val navController = rememberAnimatedNavController()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = LocalWindowInsets.current.navigationBars.bottom.transformDp)
                ) {
                    NCNavGraph(navController) {
                        finish()
                    }

                    // 底部播放器组件
                    CpnBottomPlayMusic()
                    // 音乐播放Sheet
                    PlayMusicSheet()
                    // 播放列表Sheet
                    PlayListSheet()
                }
                FixSystemBarsColor()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitProcess(0)
    }
}

