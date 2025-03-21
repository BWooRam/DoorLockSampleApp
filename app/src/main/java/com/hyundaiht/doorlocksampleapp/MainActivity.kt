package com.hyundaiht.doorlocksampleapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.hyundaiht.doorlocksampleapp.biometrics.BiometricsTestActivity
import com.hyundaiht.doorlocksampleapp.property.PropertyTestActivity
import com.hyundaiht.doorlocksampleapp.property.PropertyUiTestActivity
import com.hyundaiht.doorlocksampleapp.push.PushTestActivity
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        TextWithButton("생체 인증 테스트 이동") {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    BiometricsTestActivity::class.java
                                )
                            )
                        }

                        TextWithButton("Push 저장 테스트 이동") {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    PushTestActivity::class.java
                                )
                            )
                        }

                        TextWithButton("Device Property 테스트 이동") {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    PropertyUiTestActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
