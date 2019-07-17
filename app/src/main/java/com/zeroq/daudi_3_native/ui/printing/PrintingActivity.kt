package com.zeroq.daudi_3_native.ui.printing

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.services.BluetoothService
import kotlinx.android.synthetic.main.activity_printing.*
import kotlinx.android.synthetic.main.activity_truck_detail.*
import timber.log.Timber

class PrintingActivity : BaseActivity() {

    lateinit var printingViewModel: PrintingViewModel

    private var mService: BluetoothService? = null


    companion object {
        private val REQUEST_ENABLE_BT = 2
        private val REQUEST_CONNECT_DEVICE = 1

        fun startPrintingActivity(context: Context) {
            context.startActivity(Intent(context, PrintingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printing)

        printingViewModel = getViewModel(PrintingViewModel::class.java)

        /*
        * bluetooth service
        * */
        checkBluetoothState()

    }


    override fun onStart() {
        super.onStart()

        /**
         * check if bluetooth is on
         * */
        if (mService!!.isBTopen) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else {
            btnClose!!.isEnabled = false
            btn_print!!.isEnabled = false
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        /**
         * clear service
         * */
        if (mService != null)
            mService!!.stop()

        mService = null
    }

    private fun checkBluetoothState() {
        mService = BluetoothService(this, Handler { msg ->
            when (msg.what) {
                BluetoothService.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothService.STATE_CONNECTED -> {
                        Toast.makeText(
                            applicationContext, "Connect successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnClose!!.isEnabled = true
                        btnPrint!!.isEnabled = true
                    }
                    BluetoothService.STATE_CONNECTING -> {
                        Timber.d("Connecting")
                    }

                    BluetoothService.STATE_LISTEN, BluetoothService.STATE_NONE -> {
                        Timber.d("State None")
                    }
                }


                BluetoothService.MESSAGE_CONNECTION_LOST -> {
                    Toast.makeText(
                        applicationContext, "Device connection was lost",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnClose!!.isEnabled = false
                    btnPrint!!.isEnabled = false
                }
                BluetoothService.MESSAGE_UNABLE_CONNECT -> {
                    Toast.makeText(
                        applicationContext, "Unable to connect device",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            return@Handler true
        })


        if (!mService!!.isAvailable) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            finish()
        }
    }

}
