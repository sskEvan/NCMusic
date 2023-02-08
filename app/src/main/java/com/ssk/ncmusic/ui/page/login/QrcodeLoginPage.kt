package com.ssk.ncmusic.ui.page.login

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssk.ncmusic.R
import com.ssk.ncmusic.core.AppGlobalData
import com.ssk.ncmusic.core.nav.NCNavController
import com.ssk.ncmusic.core.nav.RouterUrls
import com.ssk.ncmusic.core.viewstate.BaseViewStateViewModel
import com.ssk.ncmusic.core.viewstate.LoadingComponent
import com.ssk.ncmusic.http.api.NCApi
import com.ssk.ncmusic.model.LoginResult
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.QrcodeUtil
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.toPx
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Created by ssk on 2023/2/8.
 */
@Composable
fun QrcodeLoginPage() {
    val viewModel: QrcodeLoginViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        viewModel.qrcodeAuth()
    }
    LaunchedEffect(viewModel.qrcodeAuthStatus) {
        if (viewModel.qrcodeAuthStatus == 800) {  // 二维码过期，重走认证流程
            Log.e("ssk", "----二维码过期，重新生成")
            viewModel.qrcodeAuth()
        }
    }
    LaunchedEffect(viewModel.getAccountInfoSuccess) {
        if (viewModel.getAccountInfoSuccess == true) {
            NCNavController.instance.popBackStack()
            NCNavController.instance.navigate(RouterUrls.HOME)
        } else if (viewModel.getAccountInfoSuccess == false) {  // 获取用户信息失败，重走认证流程
            viewModel.qrcodeAuth()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.primary),
        horizontalAlignment = Alignment.CenterHorizontally
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

        Column(
            modifier = Modifier
                .padding(top = 100.cdp)
                .width(540.cdp)
                .background(AppColorsProvider.current.pure, shape = RoundedCornerShape(16.cdp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "扫码登录体验",
                fontSize = 40.csp,
                color = AppColorsProvider.current.firstText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.cdp, bottom = 16.cdp)
            )
            if (viewModel.qrcodeAuthStatus == 801 || viewModel.qrcodeAuthStatus == 802) {
                Image(
                    bitmap = viewModel.qrcodeBitmap!!.asImageBitmap(),
                    modifier = Modifier.size(400.cdp),
                    contentDescription = "登录二维码"
                )
            } else {
                Box(modifier = Modifier.size(400.cdp), contentAlignment = Alignment.Center) {
                    LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
                }
            }

            val tip = when (viewModel.qrcodeAuthStatus) {
                801, 802 -> "请使用网易云音乐app扫码授权登录"
                803 -> "正在获取用户信息..."
                else -> "正在加载二维码"
            }

            Text(
                text = tip,
                fontSize = 28.csp,
                textAlign = TextAlign.Center,
                color = AppColorsProvider.current.secondIcon,
                modifier = Modifier.padding(top = 16.cdp, start = 32.cdp, end = 32.cdp)
            )
            Text(
                text = "(仅供学习使用)",
                fontSize = 28.csp,
                textAlign = TextAlign.Center,
                color = AppColorsProvider.current.secondIcon,
                modifier = Modifier.padding(bottom = 32.cdp, start = 32.cdp, end = 32.cdp)
            )
        }
    }
}

@HiltViewModel
class QrcodeLoginViewModel @Inject constructor(private val api: NCApi) : BaseViewStateViewModel() {

    var qrcodeAuthStatus by mutableStateOf<Int?>(null)
    var qrcodeBitmap by mutableStateOf<Bitmap?>(null)
    var getAccountInfoSuccess by mutableStateOf<Boolean?>(null)
    private var mLastQrcodeAuthJob: Job? = null
    private var mCookie = ""

    fun qrcodeAuth() {
        mLastQrcodeAuthJob?.let {
            Log.d("ssk", "----重新授权，取消上次任务")
            it.cancel()
        }
        qrcodeAuthStatus = null
        mLastQrcodeAuthJob = launch {
            val qrcodeKeyResult = api.getLoginQrcodeKey()
            val qrcodeValueResult = api.getLoginQrcodeValue(qrcodeKeyResult.data.unikey)
            qrcodeBitmap = QrcodeUtil.createQrcodeBitmap(
                qrcodeValueResult.data.qrurl,
                360.cdp.toPx.toInt(),
                360.cdp.toPx.toInt()
            )
            var qrcodeAuthResult = api.checkQrcodeAuthStatus(qrcodeKeyResult.data.unikey)
            qrcodeAuthStatus = qrcodeAuthResult.code
            while (mLastQrcodeAuthJob?.isActive != false) {
                // 4s轮训一次登录授权状态
                delay(4000)
                qrcodeAuthResult = api.checkQrcodeAuthStatus(qrcodeKeyResult.data.unikey)
                qrcodeAuthStatus = qrcodeAuthResult.code
                if (qrcodeAuthResult.resultOk()) {  // 授权成功
                    Log.d("ssk", "----授权成功")
                    mCookie = qrcodeAuthResult.cookie
                    getAccountInfo()
                    break
                } else if (qrcodeAuthStatus == 800) {
                    Log.d("ssk", "----二维码过期")
                } else if (qrcodeAuthStatus == 801) {
                    Log.d("ssk", "----等待扫码")
                } else if (qrcodeAuthStatus == 802) {
                    Log.d("ssk", "----待确认")
                }
            }
            Log.d("ssk", "授权成功----请求个人信息和账户信息")
            qrcodeAuthResult
        }
    }

    private suspend fun getAccountInfo() {
        val accountInfoResult = api.getAccountInfo(mCookie)
        if (accountInfoResult.resultOk()) {
            val loginResult = LoginResult(accountInfoResult.account, accountInfoResult.profile, mCookie)
            AppGlobalData.sLoginResult = loginResult
            AppGlobalData.sLoginRefreshFlag = !AppGlobalData.sLoginRefreshFlag
            getAccountInfoSuccess = true
        } else {
            getAccountInfoSuccess = false
        }
    }
}