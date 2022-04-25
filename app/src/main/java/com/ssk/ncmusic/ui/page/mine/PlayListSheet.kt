package com.ssk.ncmusic.ui.page

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.ssk.ncmusic.ui.page.mine.component.CpnCurrentPlayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/25.
 */

var showPlayListSheet by mutableStateOf(false)

@Composable
fun PlayListSheet() {
    if (showPlayListSheet) {
        PlayListSheetContent()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayListSheetContent() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 300),
        skipHalfExpanded = true,
        confirmStateChange = {
            Log.e("ssk", "PlayListSheetContent confirmStateChange=${it}")
            scope.launch {
                delay(200)
                showPlayListSheet = it == ModalBottomSheetValue.Expanded
            }
            true
        }
    )
    LaunchedEffect(showPlayListSheet) {
        if (showPlayListSheet) {
            sheetState.show()
        }
    }

    BackHandler(enabled = showPlayListSheet) {
        scope.launch {
            sheetState.hide()
            showPlayListSheet = false
        }
    }
    ModalBottomSheetLayout(
        sheetContent = { CpnCurrentPlayList() },
        sheetState = sheetState,
        sheetBackgroundColor = Color.Transparent
    ) {
    }
}