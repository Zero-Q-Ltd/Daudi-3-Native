package com.zeroq.daudi_3_native.ui.login

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.zeroq.daudi_3_native.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
import androidx.lifecycle.ViewModelProvider
import com.zeroq.daudi_3_native.viewmodel.UserViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseUser
import com.zeroq.daudi_3_native.viewmodel.AuthenticationViewModel
import com.zeroq.daudi_3_native.vo.Status


class LoginActivity : DaggerAppCompatActivity() {

    private lateinit var fireAuthListener: FirebaseAuth.AuthStateListener

    // init firebase auth

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient


    lateinit var authenticationViewModel: AuthenticationViewModel
    lateinit var userViewModel: UserViewModel


    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authenticationViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(AuthenticationViewModel::class.java)

        userViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(UserViewModel::class.java)

        sign_in_button.setOnClickListener { signIn() }

        /**
         * Authentication response
         * */
        authenticationViewModel.loginData.observe(this, Observer {

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
    }


    override fun onStart() {
        super.onStart()
        fireAuthListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {
                // # TODO: do something
                userViewModel.setAdminId(it.uid.toString()).getAdmin().observe(this, Observer { t ->
                    if (t.isSuccessful) {
                        Timber.d("xxx" + t.data()?.email)
                    } else {
                        Timber.e("Major error occurred")
                    }
                })
            }
        }
        firebaseAuth.addAuthStateListener(fireAuthListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authenticationViewModel.onResultFromActivity(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 200)
    }
}