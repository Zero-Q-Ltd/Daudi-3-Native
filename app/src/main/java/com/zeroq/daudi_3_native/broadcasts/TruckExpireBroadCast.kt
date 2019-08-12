package com.zeroq.daudi_3_native.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import timber.log.Timber

class TruckExpireBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       Timber.d("Intent Detected")
    }
}