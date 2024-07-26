package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import com.kakao.sdk.common.KakaoSdk;

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupListMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        KakaoSdk.init(this, getString(R.string.kakao_app_key));
    }
}
