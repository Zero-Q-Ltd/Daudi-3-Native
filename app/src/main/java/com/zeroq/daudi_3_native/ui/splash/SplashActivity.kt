package com.zeroq.daudi_3_native.ui.splash

import android.os.Bundle
import com.zeroq.daudi_3_native.ui.MainActivity
import dagger.android.support.DaggerAppCompatActivity

class SplashActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivity.startActivity(this)
        finish()
    }
}