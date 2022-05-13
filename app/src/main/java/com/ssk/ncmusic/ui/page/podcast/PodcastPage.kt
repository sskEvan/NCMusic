package com.ssk.ncmusic.ui.page.podcast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DrawerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.R
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun PodcastPage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .statusBarsPadding()
            .fillMaxSize(),
    ) {
        CommonTopAppBar(title = "播客",
            leftIconResId = R.drawable.ic_drawer_toggle,
            leftClick = {
                scope.launch {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            })
        Text("播客")
    }
}