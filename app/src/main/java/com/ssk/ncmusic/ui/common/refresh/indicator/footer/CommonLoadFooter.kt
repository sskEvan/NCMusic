package com.ssk.ncmusic.ui.common.refresh.indicator.footer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.ssk.ncmusic.core.viewstate.LoadingComponent
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/1/10.
 * Description-> 经典上拉加载更多footer
 */
@Composable
fun CommonLoadFooter() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.cdp)
    ) {
        val (ivLoading, tvStatus) = createRefs()
        createHorizontalChain(ivLoading, tvStatus, chainStyle = ChainStyle.Packed)

        LoadingComponent(modifier = Modifier.wrapContentSize()
            .padding(end = 20.cdp)
            .constrainAs(ivLoading) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start)
                end.linkTo(tvStatus.start)
                bottom.linkTo(parent.bottom)
            },
            loadingWidth = 30.cdp,
            loadingHeight = 30.cdp,
            color = AppColorsProvider.current.secondIcon)

        Text(
            text = "正在加载...",
            fontSize = 30.csp,
            color = AppColorsProvider.current.secondText,
            modifier = Modifier.constrainAs(tvStatus) {
                end.linkTo(parent.end)
                start.linkTo(ivLoading.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                centerVerticallyTo(parent)
            }
        )
    }
}
