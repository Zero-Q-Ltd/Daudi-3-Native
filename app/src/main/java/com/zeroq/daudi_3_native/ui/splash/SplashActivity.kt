package com.zeroq.daudi_3_native.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.activate.ActivateActivity
import com.zeroq.daudi_3_native.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        requestPermissions()

        // get viewmodel
        viewModel = getViewModel(SplashViewModel::class.java)

        operations()
    }

    private fun operations() {
        var signTrigger = false
        viewModel.isSignedIn().observe(this, Observer {
            signTrigger = !signTrigger
            showProgress(false)
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
            showProgress(false)
            if (adminTriggered)
                if (it.isSuccessful) {
                    MainActivity.startActivity(this)
                } else {
                    ActivateActivity.startActivity(this)
                }
            this.finish()
        })
    }

    private fun requestPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.GET_ACCOUNTS,
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.VIBRATE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.WAKE_LOCK,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {

                }
            }).check()
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}