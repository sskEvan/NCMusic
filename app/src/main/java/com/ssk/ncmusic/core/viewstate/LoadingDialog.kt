package com.ssk.ncmusic.core.viewstate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Created by ssk on 2021/9/15.
 * Description-> compose通用加载框
 */
@Composable
fun LoadingDialog(show: Boolean) {
    Dialog(onDismissRequest = { show }) {
        Card(
            backgroundColor = Color.White,
            elevation = 8.dp,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LoadingComponent()
            }
        }
    }
}