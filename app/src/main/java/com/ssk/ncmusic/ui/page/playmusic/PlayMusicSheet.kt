package com.ssk.ncmusic.ui.page.playmusic

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.ui.page.playmusic.component.CpnPlayMusic
import com.ssk.ncmusic.viewmodel.playmusic.PlayMusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/23.
 */

const val DISK_ROTATE_ANIM_CYCLE = 10000

@Composable
fun PlayMusicSheet() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    if (MusicPlayController.showPlayMusicSheet) {
        PlayMusicSheetContent()
    }
    LaunchedEffect(MusicPlayController.playMusicSheetOffset) {
        if (MusicPlayController.playMusicSheetOffset == 0 && MusicPlayController.isPlaying()) {
            viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
            viewModel.sheetDiskRotate.animateTo(
                targetValue = 360f + viewModel.lastSheetDiskRotateAngleForSnap,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = DISK_ROTATE_ANIM_CYCLE, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            viewModel.lastSheetDiskRotateAngleForSnap = viewModel.sheetDiskRotate.value
            viewModel.sheetDiskRotate.stop()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayMusicSheetContent() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            Log.e("ssk", "confirmStateChange=${it}")
            scope.launch {
                delay(200)
                MusicPlayController.showPlayMusicSheet = it == ModalBottomSheetValue.Expanded
                MusicPlayController.showCpnBottomMusicPlay = !MusicPlayController.showPlayMusicSheet
                if (!MusicPlayController.showPlayMusicSheet) {
                    viewModel.lastSheetDiskRotateAngleForSnap = 0f
                    viewModel.sheetDiskRotate.snapTo(viewModel.lastSheetDiskRotateAngleForSnap)
                    viewModel.sheetDiskRotate.stop()
                }
            }
            true
        }
    )
    LaunchedEffect(MusicPlayController.showPlayMusicSheet) {
        if (MusicPlayController.showPlayMusicSheet) {
            MusicPlayController.showCpnBottomMusicPlay = false
            sheetState.show()
            delay(300)
        }
    }

    BackHandler(enabled = true) {
        scope.launch {
            sheetState.hide()
            viewModel.lastSheetDiskRotateAngleForSnap = 0f
            viewModel.sheetDiskRotate.snapTo(0f)
            viewModel.sheetDiskRotate.stop()
            MusicPlayController.showPlayMusicSheet = false
            MusicPlayController.showCpnBottomMusicPlay = true
        }
    }
    ModalBottomSheetLayout(
        modifier = Modifier.offset { IntOffset(0, MusicPlayController.playMusicSheetOffset) },
        sheetContent = {
            CpnPlayMusic {
                scope.launch {
                    sheetState.hide()
                    viewModel.lastSheetDiskRotateAngleForSnap = 0f
                    viewModel.sheetDiskRotate.snapTo(0f)
                    viewModel.sheetDiskRotate.stop()
                    MusicPlayController.showPlayMusicSheet = false
                    MusicPlayController.showCpnBottomMusicPlay = true
                }
            }
        },
        sheetState = sheetState
    ) {
    }
}




