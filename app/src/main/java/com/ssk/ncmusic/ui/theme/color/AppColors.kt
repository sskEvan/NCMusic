package com.ssk.ncmusic.ui.theme.color

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Created by ssk on 2022/4/17.
 */
@Stable
class AppColors(
    statusBar: Color,
    pure: Color,
    primary: Color,
    primaryVariant: Color,
    secondary: Color,
    background: Color,
    firstText: Color,
    secondText: Color,
    thirdText: Color,
    firstIcon: Color,
    secondIcon: Color,
    thirdIcon: Color,
    appBarBackground: Color,
    appBarContent: Color,
    card: Color,
) {
    var statusBarColor: Color by mutableStateOf(statusBar)
        internal set
    var pure : Color by mutableStateOf(pure)
        internal set
    var primary: Color by mutableStateOf(primary)
        internal set
    var primaryVariant: Color by mutableStateOf(primaryVariant)
        internal set
    var secondary: Color by mutableStateOf(secondary)
        internal set
    var background: Color by mutableStateOf(background)
        private set
    var firstText: Color by mutableStateOf(firstText)
        private set
    var secondText: Color by mutableStateOf(secondText)
        private set
    var thirdText: Color by mutableStateOf(thirdText)
        private set
    var firstIcon: Color by mutableStateOf(firstIcon)
        private set
    var secondIcon: Color by mutableStateOf(secondIcon)
        private set
    var thirdIcon: Color by mutableStateOf(thirdIcon)
        private set
    var appBarBackground: Color by mutableStateOf(appBarBackground)
        private set
    var appBarContent: Color by mutableStateOf(appBarContent)
        private set
    var card: Color by mutableStateOf(card)
        private set
}