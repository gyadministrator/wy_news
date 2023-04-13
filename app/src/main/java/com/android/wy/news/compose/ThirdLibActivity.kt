package com.android.wy.news.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.android.wy.news.R
import com.android.wy.news.activity.WebActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class ThirdLibActivity : ComponentActivity() {

    companion object {
        fun startThirdLibActivity(context: Context) {
            val intent = Intent(context, ThirdLibActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //主题里面适配了深色模式
            /*WyNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ThirdContent(title = "第三方数据清单")
                }
            }*/
            //设置状态栏颜色
            //this.window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            //boolean :true 白色字体
            //boolean :false 黑色字体
            //不生效
            //ViewCompat.getWindowInsetsController(LocalView.current)?.isAppearanceLightStatusBars = false
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(Color.White, true)
            ThirdContent(title = "第三方数据清单")
        }
    }

    @OptIn(ExperimentalUnitApi::class)
    @Composable
    fun ThirdContent(title: String) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .background(colorResource(id = R.color.white))
                    .padding(Dp(15f))
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(Dp(25f), Dp(25f))
                        .clickable(onClick = { finish() },
                            // 去除点击效果
                            indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            })
                )
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.padding(Dp(6f)))

            Column(
                Modifier
                    .background(color = colorResource(id = R.color.white))
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(Dp(10f))
            ) {
                Text(
                    text = "第三方SDK目录",
                    Modifier.fillMaxWidth(),
                    color = Color.Black,
                    fontSize = TextUnit(
                        18f, TextUnitType.Sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(Dp(10f)))
                Text(
                    text = "使用SDK名称",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.Black,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "高德开放平台定位SDK",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.DarkGray,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    )
                )
                Text(
                    text = "第三方名称",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.Black,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "高德软件有限公司",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.DarkGray,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    )
                )
                Text(
                    text = "使用目的",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.Black,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "为了向用户提供定位服务",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.DarkGray,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    )
                )
                Text(
                    text = "收集个人信息",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.Black,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "位置信息（经纬度、精确位置、粗略位置）【通过IP 地址、GNSS信息、WiFi状态、WiFi参数、WiFi列表、SSID、BSSID、基站信息、信号强度的信息、蓝牙信息、传感器信息（矢量、加速度、压力）、设备信号强度信息获取、外部储存目录】、设备标识信息（IMEI、IDFA、IDFV、Android ID、MEID、MAC地址、OAID、IMSI、ICCID、硬件序列号）、当前应用信息（应用名、应用版本号）、设备参数及系统信息（系统属性、设备型号、操作系统、运营商信息）。",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.DarkGray,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    )
                )
                Text(
                    text = "隐私权政策链接",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f)),
                    color = Color.Black,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "https://lbs.amap.com/pages/privacy/",
                    Modifier
                        .fillMaxWidth()
                        .padding(Dp(6f))
                        .clickable(onClick = {
                            WebActivity.startActivity(
                                this@ThirdLibActivity, "https://lbs.amap.com/pages/privacy/"
                            )
                        },
                            // 去除点击效果
                            indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            }),
                    color = Color.Blue,
                    fontSize = TextUnit(
                        16f, TextUnitType.Sp
                    )
                )
            }
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun ThirdContentPreview() {
        ThirdContent(title = "第三方数据清单")
    }
}