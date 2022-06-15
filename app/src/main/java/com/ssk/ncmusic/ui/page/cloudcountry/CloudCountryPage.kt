package com.ssk.ncmusic.ui.page.cloudcountry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.DrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.viewstate.ViewStateComponent
import com.ssk.ncmusic.ui.common.CommonTabLayout
import com.ssk.ncmusic.ui.common.CommonTabLayoutStyle
import com.ssk.ncmusic.ui.common.CommonTopAppBar
import com.ssk.ncmusic.ui.page.video.VideoGridPage
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.viewmodel.cloudcountry.CloudCountryViewModel
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun CloudCountryPage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        CommonTopAppBar(title = "云村",
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

        val viewModel: CloudCountryViewModel = hiltViewModel()

        ViewStateComponent(viewStateLiveData = viewModel.videoGroupTabsResult,
            loadDataBlock = {
                viewModel.getVideoGroupTabs()
            }) { data ->

            var selectedIndex by remember { mutableStateOf(0) }
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(
                initialPage = 0,
            )

            Column {
                CommonTabLayout(
                    tabTexts = data.data.map {
                        it.name
                    },
                    style = CommonTabLayoutStyle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.cdp),
                        indicatorPaddingBottom = 24.cdp,
                        indicatorWidth = 80.cdp,
                        isScrollable = true,
                        selectedTextSize = 28.csp,
                        unselectedTextSize = 28.csp
                    ),
                    selectedIndex = selectedIndex
                ) {
                    selectedIndex = it
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }

                HorizontalPager(
                    count = data.data.size,
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColorsProvider.current.background)
                ) { pagePosition ->
                    selectedIndex = currentPage
                    VideoGridPage(data.data[pagePosition].id)
                }
            }
        }
    }
}