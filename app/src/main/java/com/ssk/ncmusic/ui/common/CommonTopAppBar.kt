package com.ssk.ncmusic.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.transformDp

/**
 * Created by ssk on 2022/4/18.
 */
@Composable
fun CommonTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    customTitleLayout: (@Composable () -> Unit)? = null,
    backgroundColor: Color = AppColorsProvider.current.appBarBackground,
    contentColor: Color = AppColorsProvider.current.appBarContent,
    leftIconResId: Int = R.drawable.ic_back,
    rightIconResId: Int = -1,
    rightText: String = "",
    customRightLayout: (@Composable () -> Unit)? = null,
    leftClick: (() -> Unit)? = null,
    rightClick: (() -> Unit)? = null,
    showBottomDivider: Boolean = false,
) {

    // 左边按钮宽度
    var leftWidth by remember {
        mutableStateOf(1)
    }

    // 右边按钮宽度
    var rightWidth by remember {
        mutableStateOf(1)
    }

    Column(modifier = modifier) {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp)
                .zIndex(1f),
            contentPadding = PaddingValues(0.dp, 0.dp),
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            elevation = 0.dp
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (leftIcon, rightLayout, titleLayout, bottomDivider) = createRefs()

                Box(modifier = Modifier
                    .constrainAs(leftIcon) {
                        start.linkTo(parent.start)
                        end.linkTo(titleLayout.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .onGloballyPositioned {
                        leftWidth = it.size.width
                    }) {

                    CommonIcon(
                        leftIconResId,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                leftClick?.invoke() ?: NCNavController.instance.popBackStack()
                            }
                            .padding(20.cdp)
                            .size(48.cdp),
                        tint = contentColor
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .constrainAs(rightLayout) {
                            start.linkTo(titleLayout.end)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .clickable {
                            rightClick?.invoke()
                        }
                        .onGloballyPositioned {
                            rightWidth = it.size.width
                        },
                    contentAlignment = Alignment.CenterEnd,
                ) {

                    if (customRightLayout != null) {
                        customRightLayout.invoke()
                    } else {
                        if (rightIconResId != -1) {
                            CommonIcon(
                                rightIconResId,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable {
                                        rightClick?.invoke()
                                    }
                                    .padding(20.cdp)
                                    .size(48.cdp),
                                tint = contentColor
                            )
                        } else if (rightText.isNotBlank()) {
                            Text(
                                text = rightText,
                                fontSize = 30.csp,
                                textAlign = TextAlign.Center,
                                color = contentColor,
                                maxLines = 1,
                                modifier = Modifier.padding(20.cdp)
                            )
                        }
                    }
                }

                val titleLeftPadding = if (leftWidth >= rightWidth) {
                    0
                } else {
                    rightWidth - leftWidth
                }

                val titleRightPadding = if (leftWidth < rightWidth) {
                    0
                } else {
                    leftWidth - rightWidth
                }

                Box(modifier = Modifier
                    .padding(start = titleLeftPadding.transformDp, end = titleRightPadding.transformDp)
                    .constrainAs(titleLayout) {
                        start.linkTo(leftIcon.end)
                        end.linkTo(rightLayout.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }) {
                    customTitleLayout?.invoke()?: Text(
                        text = title,
                        fontSize = 36.csp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (showBottomDivider) {
                    Divider(modifier = Modifier.constrainAs(bottomDivider) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }, color = Color(0xFFE5E5E5), thickness = 1.cdp)
                }
            }
        }
    }
}
