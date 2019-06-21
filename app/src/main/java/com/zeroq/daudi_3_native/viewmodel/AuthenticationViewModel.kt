package com.zeroq.daudi_3_native.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.zeroq.daudi_3_native.data.repository.FirestoreRepository
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
import javax.inject.Inject
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.zeroq.daudi_3_native.vo.MResource
import com.zeroq.daudi_3_native.vo.Resource
import com.zeroq.daudi_3_native.vo.Status


const val RC_SIGN_IN: Int = 200

class AuthenticationViewModel @Inject constructor(
    var firebaseAuth: FirebaseAuth
) : ViewModel() {

    var loginData = MutableLiveData<MResource<AuthResult>>()

    //Called from Activity receving result
    fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        loginData.value = MResource(Status.LOADING, null, "")

        when (RC_SIGN_IN) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    Timber.e(e)
                    loginData.value = MResource(Status.ERROR, null, e.message)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (!task.isSuccessful) {
                    loginData.value = MResource(Status.ERROR, null, task.exception?.message)
                    Timber.e(task.exception)
                }
                loginData.value = MResource(Status.SUCCESS, task.result, "")
            }
    }
}