package com.zeroq.daudi_3_native.ui.splash

import android.os.Bundle
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.login.LoginActivity
import dagger.android.support.DaggerAppCompatActivity

class SplashActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        LoginActivity.startActivity(this)
//        finish()
    }
}