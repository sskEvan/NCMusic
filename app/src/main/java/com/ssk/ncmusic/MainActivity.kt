package com.ssk.ncmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ssk.ncmusic.core.nav.NCNavGraph
import com.ssk.ncmusic.ui.theme.AppTheme
import com.ssk.ncmusic.ui.theme.themeTypeState
import com.ssk.ncmusic.utils.setAndroidNativeLightStatusBar
import com.ssk.ncmusic.utils.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint

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
                NCNavGraph(navController) {
                    finish()
                }
            }
        }
    }
}

