package com.ssk.ncmusic

import android.annotation.SuppressLint
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

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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

                    FixSystemBarsColor()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitProcess(0)
    }
}

