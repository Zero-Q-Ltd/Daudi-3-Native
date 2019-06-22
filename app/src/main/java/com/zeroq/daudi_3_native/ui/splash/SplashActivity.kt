package com.zeroq.daudi_3_native.ui.splash

import android.os.Bundle
import androidx.lifecycle.Observer
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.activate.ActivateActivity
import com.zeroq.daudi_3_native.ui.login.LoginActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // get viewmodel
        viewModel = getViewModel(SplashViewModel::class.java)

        operations()
    }

    fun operations() {
        viewModel.isSignedIn().observe(this, Observer {
            if (!it) {
                LoginActivity.startActivity(this)
                finish()
            }
        })

        // check if admin is allowed to go to the next step
        viewModel.getAdmin().observe(this, Observer {
            if (it.isSuccessful) {
                MainActivity.startActivity(this)
            } else {
                ActivateActivity.startActivity(this)
            }
            finish()
        })
    }
}