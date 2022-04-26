package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.ui.page.mine.component.CpnPlayMusic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/23.
 */

const val DISK_ROTATE_ANIM_CYCLE = 10000

var showCpnBottomMusicPlay by mutableStateOf(false)
var showPlayMusicSheet by mutableStateOf(false)
var sheetNeedleUp by mutableStateOf(true)
val sheetDiskRotate by mutableStateOf(Animatable(0f))
var lastSheetDiskRotateAngleForSnap = 0f

@Composable
fun PlayMusicSheet() {
    val sysUiController = rememberSystemUiController()
    if (showPlayMusicSheet) {
        sysUiController.setSystemBarsColor(color = Color.Transparent, false)
        PlayMusicSheetContent()
    } else {
        NCNavController.instance.currentBackStackEntryAsState().value?.destination?.route?.split("/")?.get(0)?.let {
            val isSystemInDarkTheme = if (it == RouterUrls.PLAY_LIST) {
                false
            } else {
                !isSystemInDarkTheme()
            }
            sysUiController.setSystemBarsColor(Color.Transparent, isSystemInDarkTheme)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayMusicSheetContent() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            Log.e("ssk", "confirmStateChange=${it}")
            scope.launch {
                delay(200)
                showPlayMusicSheet = it == ModalBottomSheetValue.Expanded
                showCpnBottomMusicPlay = !showPlayMusicSheet
                if (!showPlayMusicSheet) {
                    lastSheetDiskRotateAngleForSnap = 0f
                    sheetDiskRotate.snapTo(lastSheetDiskRotateAngleForSnap)
                    sheetDiskRotate.stop()
                }
            }
            true
        }
    )
    LaunchedEffect(showPlayMusicSheet) {
        if (showPlayMusicSheet) {
            sheetState.show()
        }
    }

    BackHandler(enabled = showPlayMusicSheet) {
        scope.launch {
            sheetState.hide()
            lastSheetDiskRotateAngleForSnap = 0f
            sheetDiskRotate.snapTo(0f)
            sheetDiskRotate.stop()
            showPlayMusicSheet = false
            showCpnBottomMusicPlay = true
        }
    }
    ModalBottomSheetLayout(
        sheetContent = {
            CpnPlayMusic {
                scope.launch {
                    sheetState.hide()
                    lastSheetDiskRotateAngleForSnap = 0f
                    sheetDiskRotate.snapTo(0f)
                    sheetDiskRotate.stop()
                    showPlayMusicSheet = false
                    showCpnBottomMusicPlay = true
                }
            }
        },
        sheetState = sheetState
    ) {
    }
}

