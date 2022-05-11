package com.ssk.ncmusic.ui.page.playmusic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import com.ssk.ncmusic.viewmodel.playmusic.PlayMusicViewModel

/**
 * Created by ssk on 2022/5/11.
 */
@Composable
fun CpnLyric() {
    val viewModel: PlayMusicViewModel = hiltViewModel()
    Box(
        modifier = Modifier
            .padding(vertical = 50.cdp)
            .fillMaxSize()
            .background(Color.Red)
            .onClick {
                viewModel.showLyric = !viewModel.showLyric
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "歌词列表", fontSize = 50.csp)
    }
}
