package com.ssk.ncmusic.ui.page.home.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.ssk.ncmusic.R
import com.ssk.ncmusic.ui.common.CommonIcon
import com.ssk.ncmusic.ui.theme.*
import com.ssk.ncmusic.ui.theme.color.palette.dark.DarkColorPalette
import com.ssk.ncmusic.ui.theme.color.palette.light.*
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/13.
 */
@Composable
fun CpnDrawerThemeSetting() {
    val anim by remember {
        mutableStateOf(Animatable(0f))
    }
    val themeListHeight = 88 * themeModels.size
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height((anim.value * themeListHeight + 88f).cdp)
    ) {
        CpnDrawerItem(
            R.drawable.ic_theme,
            R.drawable.ic_arrow_down,
            rightIconModifier = Modifier.graphicsLayer {
                rotationZ = anim.value * 180
            },
            title = buildAnnotatedString {
                withStyle(style = SpanStyle(color = AppColorsProvider.current.firstText, fontSize = 32.csp)) {
                    append("当前主题")
                }
                withStyle(style = SpanStyle(color = AppColorsProvider.current.secondText, fontSize = 28.csp)) {
                    append("（${themeModels[lastSelectedThemeIndex].value.name}）")
                }
            },
        ) {
            showThemeList = !showThemeList
            scope.launch {
                if (showThemeList) {
                    anim.animateTo(1f, tween(500))
                } else {
                    anim.animateTo(0f, tween(500))
                }
            }

        }
        if (anim.value != 88f || !showThemeList) {
            themeModels.forEachIndexed { index, themeModel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.cdp)
                        .onClick {
                            themeTypeState.value = themeModel.value.themeType

                            themeModels[lastSelectedThemeIndex].value = themeModels[lastSelectedThemeIndex].value.copy(selected = false)
                            lastSelectedThemeIndex = index
                            themeModels[index].value = themeModels[index].value.copy(selected = true)
                        }
                        .padding(horizontal = 32.cdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.cdp)
                            .clip(RoundedCornerShape(50))
                            .background(themeModel.value.color)
                    )

                    Text(
                        text = themeModel.value.name,
                        modifier = Modifier.padding(start = 24.cdp),
                        fontSize = 32.csp,
                        color = AppColorsProvider.current.secondText
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (themeModels[index].value.selected) {
                        CommonIcon(
                            resId = R.drawable.ic_checked,
                            modifier = Modifier.size(32.cdp),
                            tint = themeModel.value.color
                        )
                    }
                }
            }
        }
    }
}

private var showThemeList by mutableStateOf(false)
private var lastSelectedThemeIndex = 0
private val themeModels = mutableStateListOf(
    mutableStateOf(ThemeModel("默认", THEME_DEFAULT, DefaultColorPalette.primary, true)),
    mutableStateOf(ThemeModel("夜间", THEME_NIGHT, DarkColorPalette.pure, false)),
    mutableStateOf(ThemeModel("蓝色", THEME_BLUE, BlueColorPalette.primary, false)),
    mutableStateOf(ThemeModel("绿色", THEME_GREEN, GreenColorPalette.primary, false)),
    mutableStateOf(ThemeModel("橙色", THEME_ORIGIN, OriginColorPalette.primary, false)),
    mutableStateOf(ThemeModel("紫色", THEME_PURPLE, PurpleColorPalette.primary, false)),
    mutableStateOf(ThemeModel("黄色", THEME_YELLOW, YellowColorPalette.primary, false)),
    )


data class ThemeModel(val name: String, val themeType: Int, val color: Color, val selected: Boolean)