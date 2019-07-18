package com.zeroq.daudi_3_native.ui.printing

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.services.BluetoothService
import com.zeroq.daudi_3_native.ui.device_list.DeviceListActivity
import com.zeroq.daudi_3_native.utils.PrintPic
import kotlinx.android.synthetic.main.activity_printing.*
import kotlinx.android.synthetic.main.activity_truck_detail.*
import timber.log.Timber
import kotlin.experimental.and
import kotlin.experimental.or

class PrintingActivity : BaseActivity() {

    lateinit var printingViewModel: PrintingViewModel

    private var mService: BluetoothService? = null
    private var con_dev: BluetoothDevice? = null


    private lateinit var depotId: String
    private lateinit var idTruck: String


    companion object {
        private const val REQUEST_ENABLE_BT = 2
        private const val REQUEST_CONNECT_DEVICE = 1


        fun startPrintingActivity(context: Context, depotId: String, idTruck: String) {
            val intent = Intent(context, PrintingActivity::class.java)
            intent.putExtra("DEPOTID", depotId)
            intent.putExtra("IDTRUCK", idTruck)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printing)

        printingViewModel = getViewModel(PrintingViewModel::class.java)

        if (intent.extras != null) {
            depotId = intent.getStringExtra("DEPOTID")
            idTruck = intent.getStringExtra("IDTRUCK")
        }


        /*
        * bluetooth service
        * */
        checkBluetoothState()
        operationBtns()

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
            btnClose?.isEnabled = false
            btn_print?.isEnabled = false
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

    private fun operationBtns() {
        btn_print?.setOnClickListener(clickEvent())
        btnSearch?.setOnClickListener(clickEvent())
        btnClose?.setOnClickListener(clickEvent())
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
                        btn_print!!.isEnabled = true
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

    internal inner class clickEvent : View.OnClickListener {
        override fun onClick(v: View) {
            when (v) {
                btnSearch -> {
                    val intent = Intent(this@PrintingActivity, DeviceListActivity::class.java)
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE)
                }

                btnClose -> {
                    mService?.stop()
                }

                btn_print -> {
                    val msg = "Tel : 0733474703       www.emkaynow.com"
                    val lang = getString(R.string.strLang)

                    printImage()

                    val cmd = ByteArray(3)
                    cmd[0] = 0x1b
                    cmd[1] = 0x21

                    if (lang.compareTo("en") == 0) {
                        cmd[2] = cmd[2] or 0x10
                        mService!!.write(cmd)
                        mService!!.sendMessage("", "GBK")
                        cmd[2] = cmd[2] and 0xEF.toByte()
                        mService!!.write(cmd)
                        mService!!.sendMessage(msg + "\n\n", "GBK")
                    }


                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth open successful", Toast.LENGTH_LONG).show()
                } else {
                    finish()
                }
            }

            REQUEST_CONNECT_DEVICE -> {

                if (resultCode == Activity.RESULT_OK) {
                    val address = data?.extras!!
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)

                    con_dev = mService!!.getDevByMac(address)

                    // connect
                    mService!!.connect(con_dev)
                }
            }
        }

    }

    @SuppressLint("SdCardPath")
    private fun printImage() {
        var sendData: ByteArray? = null
        val pg = PrintPic()
        pg.initCanvas(537)
        pg.initPaint()
        pg.drawImage(0f, 0f, Environment.getExternalStorageDirectory().absolutePath + "/Emkaynow/0.png")
        sendData = pg.printDraw()
        mService!!.write(sendData)

        /**
         *
         * */
        printingViewModel.setPrintedState(depotId, idTruck).observe(this, Observer {
            if (!it.isSuccessful) {
                Timber.e(it.error())
            }
        })
    }

}
