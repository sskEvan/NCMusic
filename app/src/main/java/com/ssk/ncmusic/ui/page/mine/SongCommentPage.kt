package com.ssk.ncmusic.ui.page.mine

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.theme.AppColorsProvider

/**
 * Created by ssk on 2022/4/28.
 */
@Composable
fun SongCommentPage(songBean: SongBean) {

    BackHandler(true) {
        NCNavController.instance.popBackStack()
        MusicPlayController.playMusicSheetOffset = 0
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppColorsProvider.current.background)
    ) {
        CommonTopAppBar(title = "评论",
            titleAlign = TextAlign.Start,
            leftClick = {
                NCNavController.instance.popBackStack()
                MusicPlayController.playMusicSheetOffset = 0
            })
        Text(text = "${songBean.name}")
    }
}