package com.ssk.ncmusic.ui.page.podcast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ssk.ncmusic.ui.common.CommonTopAppBar

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun PodcastPage() {
    Column(Modifier.fillMaxSize()) {
        CommonTopAppBar(title = "博客")
        Text("博客")
    }
}