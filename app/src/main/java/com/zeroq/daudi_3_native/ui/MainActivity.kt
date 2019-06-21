package com.zeroq.daudi_3_native.ui


import android.content.Context
import android.os.Bundle
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.utils.Info
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import android.content.Intent


class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var info: Info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
