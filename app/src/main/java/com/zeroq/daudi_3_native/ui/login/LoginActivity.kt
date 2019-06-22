package com.zeroq.daudi_3_native.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.zeroq.daudi_3_native.R
import kotlinx.android.synthetic.main.activity_login.*
import com.zeroq.daudi_3_native.viewmodel.UserViewModel
import javax.inject.Inject
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.ui.activate.ActivateActivity

import com.zeroq.daudi_3_native.vo.Status


class LoginActivity : BaseActivity() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var loginViewModel: LoginViewModel
    lateinit var userViewModel: UserViewModel


    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = getViewModel(LoginViewModel::class.java)
        userViewModel = getViewModel(UserViewModel::class.java)

        sign_in_button.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, loginViewModel.RC_SIGN_IN)
        }

        operations()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginViewModel.onResultFromActivity(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    var loginInvoked = false
    private fun operations() {
        /**
         * Authentication response
         * */
        loginViewModel.getLogin().observe(this, Observer {

            if (it.status == Status.LOADING) {
                progressBar2.visibility = View.VISIBLE
            } else {
                progressBar2.visibility = View.GONE
            }

            when (it.status) {
                Status.SUCCESS ->
                    Snackbar.make(main_layout, "Logged in successfully", Snackbar.LENGTH_SHORT).show()

                Status.ERROR ->
                    Snackbar.make(main_layout, "Sorry an error occured, try again", Snackbar.LENGTH_SHORT).show()
            }
        })

        loginViewModel.getUser().observe(this, Observer {
            loginInvoked = !loginInvoked
            if (loginInvoked)
                if (it.isSuccessful) {
                    MainActivity.startActivity(this)
                } else {
                    ActivateActivity.startActivity(this)
                }
            this.finish()
        })
    }

}