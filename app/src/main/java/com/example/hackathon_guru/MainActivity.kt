package com.example.hackathon_guru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hackathon_guru.databinding.ActivityGroupListMainBinding
import com.kakao.sdk.common.KakaoSdk;

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupListMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        KakaoSdk.init(this, getString(R.string.kakao_app_key));
    }
}
