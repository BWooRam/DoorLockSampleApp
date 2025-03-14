package com.hyundaiht.doorlocksampleapp.biometrics

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.hyundaiht.doorlocksampleapp.TextWithButton
import com.hyundaiht.doorlocksampleapp.ui.theme.DoorLockSampleAppTheme

class BiometricsTestActivity : FragmentActivity() {
    private val tag = javaClass.simpleName
    private lateinit var biometric: Biometric
    private val testChallenge = "d89s7f89sd7f9s8df"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometric = object : BiometricImp() {
            override fun getBiometricPromptAuthenticationCallback(): BiometricPrompt.AuthenticationCallback {
                return object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Log.d(
                            tag,
                            "onAuthenticationSucceeded cryptoObject = ${result.cryptoObject}"
                        )
                        Log.d(
                            tag,
                            "onAuthenticationSucceeded signature = ${result.cryptoObject?.signature}"
                        )
                        Log.d(
                            tag,
                            "onAuthenticationSucceeded cipher = ${result.cryptoObject?.cipher}"
                        )
                        Log.d(tag, "onAuthenticationSucceeded mac = ${result.cryptoObject?.mac}")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Log.d(
                                tag,
                                "onAuthenticationSucceeded identityCredential = ${result.cryptoObject?.identityCredential}"
                            )
                        }
                        val signedData = SignatureHelper.signData(testChallenge)
                        Log.d(tag, "onAuthenticationSucceeded signedData = $signedData")
                        val verifyData = SignatureHelper.verifyData(testChallenge, signedData!!)
                        Log.d(tag, "onAuthenticationSucceeded verifyData = $verifyData")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Log.d(tag, "onAuthenticationFailed")
                    }
                }
            }

            override fun getPromptInfo(): BiometricPrompt.PromptInfo {
                return BiometricPrompt.PromptInfo.Builder()
                    .setTitle("생체 인증 필요")
                    .setSubtitle("앱을 사용하려면 생체 인증을 해주세요.")
                    .setNegativeButtonText("취소")
                    .build()
            }
        }.apply {
            setUp(this@BiometricsTestActivity)
        }


        enableEdgeToEdge()
        setContent {
            DoorLockSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        var rememberString by remember { mutableStateOf(KeystoreHelper.KEY_ALIAS) }
                        TextField(value = rememberString, onValueChange = { input ->
                            rememberString = input
                        })
                        TextWithButton("생체 인증 실행") {
                            val cryptoObject = SignatureHelper.getCryptoObject()
                            Log.d(tag, "cryptoObject = $cryptoObject")
                            Log.d(tag, "cryptoObject mac = ${cryptoObject.mac}")
                            Log.d(tag, "cryptoObject cipher = ${cryptoObject.cipher}")
                            Log.d(tag, "cryptoObject signature = ${cryptoObject.signature}")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                Log.d(
                                    tag,
                                    "cryptoObject identityCredential = ${cryptoObject.identityCredential}"
                                )
                            }
                            biometric.authenticate(cryptoObject)
                        }

                        TextWithButton("생체 인증 - Signature 초기화") {
                            SignatureHelper.initSign()
                        }

                        TextWithButton("암호화 키 생성") {
                            KeystoreHelper.generateKeyPair(rememberString)
                        }

                        TextWithButton("암호화 키 확인") {
                            val keyPair = KeystoreHelper.getKeyPair(rememberString)
                            Log.d(tag, "keyPair private = ${keyPair?.private}")
                            Log.d(tag, "keyPair public = ${keyPair?.public}")
                        }

                        TextWithButton("암호화 키 유효성 검증 - isKeyValid") {
                            val isKeyValid = KeystoreHelper.isKeyValid(rememberString)
                            Log.d(tag, "isKeyValid = $isKeyValid")
                        }

                        TextWithButton("암호화 키 유효성 검증 - isKeyAccessible") {
                            val isKeyAccessible = KeystoreHelper.isKeyAccessible(rememberString)
                            Log.d(tag, "isKeyAccessible = $isKeyAccessible")
                        }

                        TextWithButton("암호화 키 유효성 검증 - isKeyUsable") {
                            val isKeyUsable = KeystoreHelper.isKeyUsable(rememberString)
                            Log.d(tag, "isKeyUsable = $isKeyUsable")
                        }
                    }
                }
            }
        }
    }
}