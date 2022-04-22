package com.ssk.ncmusic.ui.page.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ssk.ncmusic.R
import com.ssk.ncmusic.api.NCApi
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateLoadingDialogComponent
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.MD5Util
import com.ssk.ncmusic.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by ssk on 2022/4/17.
 */
@Composable
fun LoginPage() {
    val viewModel: LoginViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val sysUiController = rememberSystemUiController()
    sysUiController.setSystemBarsColor(
        Color.Transparent,
        darkIcons = false,
    )

    ViewStateLoadingDialogComponent(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.primary),
        viewStateLiveData = viewModel.loginResult,
        successBlock = {
            NCNavController.instance.popBackStack()
            NCNavController.instance.navigate(RouterUrls.HOME)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = "splashLogo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 80.dp)
                    .size(100.dp)
                    .clip(CircleShape)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("请输入用户名") },
                modifier = Modifier
                    .padding(top = 40.dp)
                    .focusTarget(),
                singleLine = true,
                colors = LoginTextFieldColors()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("请输入密码") },
                modifier = Modifier.padding(top = 20.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = LoginTextFieldColors()
            )

            Button(
                onClick = {
                    viewModel.login(username, password)
                },
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 40.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                content = {
                    Text(text = "登陆", fontSize = 18.sp, color = Color.Black)
                }
            )
        }

    }
}

@Composable
private fun LoginTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        placeholderColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        unfocusedBorderColor = Color.White,
        focusedBorderColor = Color.White,
        textColor = Color.White,
        cursorColor = Color.White,
    )
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    val loginResult = ViewStateMutableLiveData<LoginResult>()

    fun login(username: String, password: String) {
        if (username.isEmpty()) {
            showToast("请输入用户名")
            return
        }
        if (password.isEmpty()) {
            showToast("请输入密码")
            return
        }
        launch(loginResult) {
            val result = api.login(username, "", MD5Util.encode(password))
            AppGlobalData.sLoginResult = result
            result
        }
    }
}