package com.ssk.ncmusic.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp

/**
 * Created by ssk on 2022/4/18.
 * @Description-> compose版本通用TabLayout
 * @param selectedIndex: 当前选中的tab索引
 * @param tabTexts： tab文本集合
 * @param style： 样式配置
 * @param modifier： 修饰
 * @param onTabSelected： 选中回调
 */
@Composable
fun CommonTabLayout(
    selectedIndex: Int = 0,
    tabTexts: List<String>,
    backgroundColor: Color = AppColorsProvider.current.background,  // 背景颜色
    selectedTextColor: Color = AppColorsProvider.current.firstText,  // 选中tab字体颜色
    unselectedTextColor: Color = AppColorsProvider.current.secondText,  // 未选中tab字体颜色
    indicatorColor: Brush = Brush.horizontalGradient(listOf(AppColorsProvider.current.primary, AppColorsProvider.current.secondary)),  // 指示器颜色
    style: CommonTabLayoutStyle = CommonTabLayoutStyle(),
    onTabSelected: ((index: Int) -> Unit)? = null
) {
    if (style.isScrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            modifier = style.modifier,
            edgePadding = 0.dp,
            backgroundColor = backgroundColor,
            indicator = @Composable { tabPositions ->
                if (style.customIndicator != null) {
                    style.customIndicator.invoke(tabPositions[selectedIndex], selectedIndex)
                } else {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedIndex])
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Divider(
                            modifier = Modifier
                                .width(style.indicatorWidth)
                                .padding(bottom = style.indicatorPaddingBottom)
                                .background(
                                    brush = indicatorColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            thickness = style.indicatorHeight,
                            color = Color.Transparent
                        )
                    }
                }
            },
            divider = @Composable {
                Divider(color = Color.Transparent)
            }
        ) {
            tabTexts.forEachIndexed { i, tabText ->
                var fontWeight = FontWeight.Normal
                if (selectedIndex == i) {
                    if (style.selectedTextBold) {
                        fontWeight = FontWeight.Bold
                    }
                } else {
                    if (style.unselectedTextBold) {
                        fontWeight = FontWeight.Bold
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onTabSelected?.invoke(i)
                                }
                            )
                        }
                        .zIndex(1f)
                        .drawBehind {
                            style.tabItemDrawBehindBlock?.invoke(this, i)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabText,
                        fontSize = if (selectedIndex == i) style.selectedTextSize else style.unselectedTextSize,
                        fontWeight = fontWeight,
                        color = if (selectedIndex == i) selectedTextColor else unselectedTextColor,
                        textAlign = TextAlign.Center,
                    )
                }

            }
        }
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = style.modifier,
            backgroundColor = backgroundColor,
            indicator = @Composable { tabPositions ->
                if(style.customIndicator != null) {
                    style.customIndicator.invoke(tabPositions[selectedIndex], selectedIndex)
                }else {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedIndex])
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Divider(
                            modifier = Modifier
                                .width(style.indicatorWidth)
                                .padding(bottom = style.indicatorPaddingBottom)
                                .background(
                                    brush = indicatorColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            thickness = style.indicatorHeight,
                            color = Color.Transparent
                        )
                    }
                }
            },
            divider = @Composable {
                Divider(color = Color.Transparent)
            }
        ) {
            tabTexts.forEachIndexed { i, tabText ->
                var fontWeight = FontWeight.Normal
                if (selectedIndex == i) {
                    if (style.selectedTextBold) {
                        fontWeight = FontWeight.Bold
                    }
                } else {
                    if (style.unselectedTextBold) {
                        fontWeight = FontWeight.Bold
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onTabSelected?.invoke(i)
                                }
                            )
                        }
                        .zIndex(1f)
                        .drawBehind {
                            style.tabItemDrawBehindBlock?.invoke(this, i)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabText,
                        fontSize = if (selectedIndex == i) style.selectedTextSize else style.unselectedTextSize,
                        fontWeight = fontWeight,
                        color = if (selectedIndex == i) selectedTextColor else unselectedTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 通用TabBar样式
 */
data class CommonTabLayoutStyle(
    val modifier: Modifier = Modifier, // 修饰
    val selectedTextSize: TextUnit = 36.csp,  // 选中tab字体大小
    val unselectedTextSize: TextUnit = 36.csp,  // 未选中tab字体大小
    val selectedTextBold: Boolean = true,  // 选中tab字体加粗
    val unselectedTextBold: Boolean = false, // 未选中tab字体加粗
    val indicatorWidth: Dp = 140.cdp,  // 指示器宽度
    val indicatorHeight: Dp = 12.cdp,  // 指示器高度
    val indicatorPaddingBottom: Dp = 0.cdp,  // 指示器高度
    val isScrollable: Boolean = true,  // 是否可滑动
    val tabItemDrawBehindBlock: (DrawScope.(position: Int) -> Unit)? = null, // tab item modifier drawBehind callback
    val customIndicator: @Composable ((selectedTabPosition: TabPosition, selectedPosition: Int) -> Unit)? = null  // 自定义指示器
)
