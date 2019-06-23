package com.zeroq.daudi_3_native.ui.splash

import android.os.Bundle
import androidx.lifecycle.Observer
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.activate.ActivateActivity
import com.zeroq.daudi_3_native.ui.login.LoginActivity

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // get viewmodel
        viewModel = getViewModel(SplashViewModel::class.java)

        operations()
    }

    private fun operations() {
        var signTrigger = false
        viewModel.isSignedIn().observe(this, Observer {
            signTrigger = !signTrigger
            if (signTrigger)
                if (!it) {
                    LoginActivity.startActivity(this)
                    this.finish()
                }
        })

        // check if admin is allowed to go to the next step
        var adminTriggered: Boolean = false
        viewModel.getAdmin().observe(this, Observer {
            adminTriggered = !adminTriggered
            if (adminTriggered)
                if (it.isSuccessful) {
                    MainActivity.startActivity(this)
                } else {
                    ActivateActivity.startActivity(this)
                }
            this.finish()
        })
    }
}