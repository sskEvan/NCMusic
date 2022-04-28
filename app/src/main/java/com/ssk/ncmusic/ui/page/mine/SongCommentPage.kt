package com.ssk.ncmusic.ui.page.mine

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.insets.statusBarsPadding
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.model.SongBean
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.page.showPlayListSheet
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/28.
 */
@Composable
fun SongCommentPage(songBean: SongBean) {

    BackHandler(true) {
        showPlayMusicSheet = true
        NCNavController.instance.popBackStack()
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
                showPlayMusicSheet = true
                NCNavController.instance.popBackStack()
            })
        Text(text = "${songBean.name}")
    }
}