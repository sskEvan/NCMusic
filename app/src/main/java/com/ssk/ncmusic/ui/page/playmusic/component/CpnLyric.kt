package com.ssk.ncmusic.ui.page.playmusic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 50.cdp)
            .fillMaxSize()
            .onClick(enableRipple = false) {
                viewModel.showLyric = !viewModel.showLyric
            },
    ) {
        items(100) {
            Text(
                text = "歌词item${it}", fontSize = 30.csp, color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.cdp)
            )
        }
    }
}
