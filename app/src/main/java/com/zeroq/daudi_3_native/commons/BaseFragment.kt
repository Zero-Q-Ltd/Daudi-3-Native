package com.zeroq.daudi_3_native.commons

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection

@SuppressLint("Registered")
open class BaseFragment: Fragment() {

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}