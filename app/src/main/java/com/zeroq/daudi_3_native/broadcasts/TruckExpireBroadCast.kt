package com.zeroq.daudi_3_native.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zeroq.daudi_3_native.ui.MainActivity
import com.zeroq.daudi_3_native.utils.TruckNotification
import dagger.android.AndroidInjection
import javax.inject.Inject

class TruckExpireBroadCast : BroadcastReceiver() {

    @Inject
    lateinit var truckNotification: TruckNotification

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)

        if (intent!!.hasExtra("CONTENT")) {
            val cont = intent.getStringExtra("CONTENT")
            val title = intent.getStringExtra("TITLE")
            val requestCode = intent.getIntExtra("REQUEST_CODE", 100)

            truckNotification.showNotification(
                context!!,
                MainActivity::class.java, title, cont,
                requestCode
            )
        }

    }
}