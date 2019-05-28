package com.zeroq.daudi_3_native.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.zeroq.daudi_3_native.viewmodel.AuthenticationViewModel


class LoginActivity : DaggerAppCompatActivity() {

    private lateinit var fireAuthListener: FirebaseAuth.AuthStateListener

    // init firebase auth

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var userViewModel: UserViewModel
    lateinit var authenticationViewModel: AuthenticationViewModel


    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(UserViewModel::class.java)

        authenticationViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AuthenticationViewModel::class.java)

        sign_in_button.setOnClickListener { signIn() }
    }


    override fun onStart() {
        super.onStart()
        fireAuthListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {
                userViewModel.getUser(it.uid.toString()).observe(this, Observer { u ->
                    Timber.e("The user is: ${u.email}")
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