package com.ssk.ncmusic

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ssk.ncmusic.core.nav.NCNavGraph
import com.ssk.ncmusic.ui.page.home.component.CpnHomeDrawer
import com.ssk.ncmusic.ui.page.permission.CpnStoragePermission
import com.ssk.ncmusic.ui.page.playmusic.PlayListSheet
import com.ssk.ncmusic.ui.page.playmusic.PlayMusicSheet
import com.ssk.ncmusic.ui.page.playmusic.component.CpnBottomPlayMusic
import com.ssk.ncmusic.ui.page.video.CpnPlayVideoTest
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
    @OptIn(ExperimentalAnimationApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setAndroidNativeLightStatusBar()
        setContent {
            AppTheme(themeTypeState.value) {

                //CpnStoragePermission()
                // CpnPlayVideoTest()

                val navController = rememberAnimatedNavController()
                val scaffoldState = rememberScaffoldState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState,
                    drawerGesturesEnabled = false,
                    drawerContent = {
                        CpnHomeDrawer(scaffoldState.drawerState)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = LocalWindowInsets.current.navigationBars.bottom.transformDp)
                    ) {
                        NCNavGraph(scaffoldState, navController) {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        exitProcess(0)
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun Sample(multiplePermissionsState: MultiplePermissionsState) {
        if (multiplePermissionsState.allPermissionsGranted) {
            // If all permissions are granted, then show screen with the feature enabled
            Text("Camera and Read storage permissions Granted! Thank you!")
        } else {
            Column {
                Text(
                    getTextToShowGivenPermissions(
                        multiplePermissionsState.revokedPermissions,
                        multiplePermissionsState.shouldShowRationale
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Request permissions")
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    private fun getTextToShowGivenPermissions(
        permissions: List<PermissionState>,
        shouldShowRationale: Boolean
    ): String {
        val revokedPermissionsSize = permissions.size
        if (revokedPermissionsSize == 0) return ""

        val textToShow = StringBuilder().apply {
            append("The ")
        }

        for (i in permissions.indices) {
            textToShow.append(permissions[i].permission)
            when {
                revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                    textToShow.append(", and ")
                }
                i == revokedPermissionsSize - 1 -> {
                    textToShow.append(" ")
                }
                else -> {
                    textToShow.append(", ")
                }
            }
        }
        textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
        textToShow.append(
            if (shouldShowRationale) {
                " important. Please grant all of them for the app to function properly."
            } else {
                " denied. The app cannot function without them."
            }
        )
        return textToShow.toString()
    }
}

