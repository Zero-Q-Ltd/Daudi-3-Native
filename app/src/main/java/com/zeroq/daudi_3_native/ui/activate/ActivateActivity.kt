package com.zeroq.daudi_3_native.ui.activate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.ui.login.LoginActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_activate.*
import timber.log.Timber
import javax.inject.Inject

class ActivateActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, ActivateActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activate)
        initialize()
    }

    private fun initialize() {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        Glide.with(this)
            .load(firebaseUser?.photoUrl)
            .centerCrop()
            .placeholder(R.drawable.place_holder)
            .apply(RequestOptions.circleCropTransform())
            .into(adminImageView)

        displayNameTextView.text = firebaseUser?.displayName

        // logout
        logout_btn.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    LoginActivity.startActivity(this)
                    finish()
                }
        }
    }

}
