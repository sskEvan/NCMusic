package com.ssk.ncmusic.ui.page.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.ViewStateLoadingDialogComponent
import com.ssk.ncmusic.core.viewstate.ViewStateMutableLiveData
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.MD5Util
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
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
    var loginByEmail by remember { mutableStateOf(false) }

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

            Box {
                Box(
                    Modifier
                        .padding(top = 205.cdp, start = 5.cdp)
                        .size(190.cdp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 200.cdp)
                        .size(200.cdp)
                        .clip(RoundedCornerShape(50)),
                    tint = AppColorsProvider.current.primaryVariant
                )
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("请输入用户名") },
                modifier = Modifier
                    .padding(top = 80.cdp)
                    .focusTarget(),
                singleLine = true,
                colors = LoginTextFieldColors()
            )

            Box {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("请输入密码") },
                    modifier = Modifier.padding(top = 40.cdp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = LoginTextFieldColors()
                )

            }


            Button(
                onClick = {
                    viewModel.login(username, password, loginByEmail)
                },
                modifier = Modifier
                    .padding(start = 80.cdp, end = 80.cdp, top = 80.cdp)
                    .fillMaxWidth()
                    .height(100.cdp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                content = {
                    Text(text = "登陆", fontSize = 36.csp, color = Color.Black)
                }
            )

            Row(
                modifier = Modifier
                    .padding(start = 80.cdp, end = 80.cdp, top = 20.cdp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = loginByEmail,
                    onClick = {
                        loginByEmail = !loginByEmail
                    },
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White
                    ),
                )
                Text(
                    text = "使用网易邮箱登陆",
                    color = Color.White,
                    fontSize = 24.csp,
                    modifier = Modifier.padding(end = 32.cdp)
                )
            }
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

    fun login(username: String, password: String, loginByEmail: Boolean) {
        if (username.isEmpty()) {
            showToast("请输入用户名")
            return
        }
        if (password.isEmpty()) {
            showToast("请输入密码")
            return
        }
        launch(loginResult) {
            val result = if (loginByEmail) {
                api.loginByEmail(
                    username,
                    "",
                    MD5Util.encode(password)
                )
            } else {
                api.loginByPassword(
                    username,
                    "",
                    MD5Util.encode(password)
                )
            }
            AppGlobalData.sLoginResult = result
            result
        }
    }
}