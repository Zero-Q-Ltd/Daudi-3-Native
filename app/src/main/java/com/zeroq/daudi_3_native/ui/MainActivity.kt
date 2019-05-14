package com.zeroq.daudi_3_native.ui


import android.os.Bundle
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.utils.Info
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var info: Info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test.text = info.text
    }
}
