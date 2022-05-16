package com.ssk.ncmusic.ui.page.permission

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ssk.ncmusic.ui.theme.AppColorsProvider
import com.ssk.ncmusic.utils.cdp
import com.ssk.ncmusic.utils.csp
import com.ssk.ncmusic.utils.onClick
import kotlinx.coroutines.launch

/**
 * Created by ssk on 2022/5/16.
 */
@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CpnStoragePermission() {
    val showDialog = remember {
        mutableStateOf(false)
    }


    val storagePermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)

//    scope.launch {
//        storagePermissionState.launchPermissionRequest()
////        if (!storagePermissionState.status.isGranted) {
////            showDialog.value = true
////        }
//    }
    LaunchedEffect(Unit) {
        storagePermissionState.launchPermissionRequest()
        if (!storagePermissionState.status.isGranted) {
            showDialog.value = true
        }
    }

    showPermissionDenyDialog(showDialog)

}

@Composable
private fun showPermissionDenyDialog(showDialog: MutableState<Boolean>) {
    val context = LocalContext.current
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Card(
                backgroundColor = AppColorsProvider.current.card,
                elevation = 8.dp,
                modifier = Modifier
                    .width(540.cdp)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.cdp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.cdp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "温馨提示",
                            fontSize = 36.csp,
                            fontWeight = FontWeight.Medium,
                            color = AppColorsProvider.current.firstText
                        )
                    }

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.cdp,
                        color = Color(0xFFF2F2F2)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.cdp, end = 40.cdp, top = 36.cdp, bottom = 56.cdp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "拒绝存储权限后，将影响app的正常使用",
                            fontSize = 36.csp, fontWeight = FontWeight.Medium, color = AppColorsProvider.current.firstText
                        )
                    }

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 2.cdp,
                        color = Color(0xFFF2F2F2)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.cdp)
                            .onClick {
                                (context as ComponentActivity).finish()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "知道了", fontSize = 36.csp, fontWeight = FontWeight.Medium, color = AppColorsProvider.current.firstText)
                    }
                }
            }
        }
    }

}