package com.ssk.ncmusic.ui.page.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.MusicPlayController
import com.ssk.ncmusic.ui.common.BottomNavigationBar
import com.ssk.ncmusic.ui.common.BottomNavigationItem
import com.ssk.ncmusic.ui.page.cloudcountry.CloudCountryPage
import com.ssk.ncmusic.ui.page.discovery.DiscoveryPage
import com.ssk.ncmusic.ui.page.home.component.CpnHomeDrawer
import com.ssk.ncmusic.ui.page.mine.MinePage
import com.ssk.ncmusic.ui.page.playmusic.PlayListSheet
import com.ssk.ncmusic.ui.page.playmusic.PlayMusicSheet
import com.ssk.ncmusic.ui.page.playmusic.component.CpnBottomPlayMusic
import com.ssk.ncmusic.ui.page.playmusic.component.cpnBottomMusicPlayPadding
import com.ssk.ncmusic.ui.page.podcast.PodcastPage
import com.ssk.ncmusic.ui.page.sing.SingPage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.TwoBackFinish
import com.ssk.ncmusic.utils.transformDp
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/17.
 */

private val bottomNavigationItems = listOf(
    BottomNavigationItem("发现", R.drawable.ic_discovery),
    BottomNavigationItem("播客", R.drawable.ic_podcast),
    BottomNavigationItem("我的", R.drawable.ic_mine),
    BottomNavigationItem("k歌", R.drawable.ic_sing),
    BottomNavigationItem("云村", R.drawable.ic_cloud_country),
)

var selectedHomeTabIndex by mutableStateOf(2)


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomePage(onFinish: () -> Unit = { }) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    BackHandler {
        if (scaffoldState.drawerState.isOpen) {
            scope.launch {
                scaffoldState.drawerState.close()
            }
        } else {
            if (MusicPlayController.showPlayMusicSheet) {
                MusicPlayController.showPlayMusicSheet = false
                MusicPlayController.showCpnBottomMusicPlay = true
            } else {
                TwoBackFinish().execute(onFinish)
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = true,
        drawerContent = {
            CpnHomeDrawer(scaffoldState.drawerState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background)
                .padding(bottom = LocalWindowInsets.current.navigationBars.bottom.transformDp)
        ) {
            Body(scaffoldState.drawerState)

            // 底部播放器组件
            CpnBottomPlayMusic()
            // 音乐播放Sheet
            PlayMusicSheet()
            // 播放列表Sheet
            PlayListSheet()
        }
    }
}

@Composable
private fun Body(drawerState: DrawerState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(
            initialPage = selectedHomeTabIndex,
        )

        val paddingBottom = if (MusicPlayController.showCpnBottomMusicPlay) {
            cpnBottomMusicPlayPadding
        } else {
            0.dp
        }

        HorizontalPager(
            count = bottomNavigationItems.size,
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .padding(bottom = paddingBottom)
                .weight(1f)
        ) { pagePosition ->
            selectedHomeTabIndex = pagerState.currentPage

            when (pagePosition) {
                0 -> DiscoveryPage(drawerState)
//                1 -> PodcastPage(drawerState)
                2 -> MinePage(drawerState)
                3 -> SingPage(drawerState)
                4 -> CloudCountryPage(drawerState)
            }
        }

        BottomNavigationBar(
            bottomNavigationItems,
            pagerState,
            selectedHomeTabIndex
        ) {
            selectedHomeTabIndex = it
        }
    }
}