package com.zeroq.daudi_3_native.ui.loading_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.Depot
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.utils.ImageUtil
import kotlinx.android.synthetic.main.activity_loading_order.*
import kotlinx.android.synthetic.main.toolbar.*
import net.glxn.qrgen.android.QRCode
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LoadingOrderActivity : BaseActivity() {

    lateinit var viewModel: LoadingOrderViewModel
    lateinit var depot: Depot

    @Inject
    lateinit var imageUtil: ImageUtil

    lateinit var _user: UserModel

    companion object {
        const val ID_TRUCK_EXTRA = "IDTRUCK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_order)

        /**
         * hide keyboad
         * */
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setUpToolbar()
        viewModel = getViewModel(LoadingOrderViewModel::class.java)

        if (intent.hasExtra(ID_TRUCK_EXTRA)) {
            val idTruck = intent.getStringExtra(ID_TRUCK_EXTRA)
            viewModel.setTruckId(idTruck)
        }

        logic()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "GatePass"
    }

    private fun logic() {

        viewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                _user = it.data()!!
                viewModel.setDepotId(_user.config?.depotdata?.depotid!!)
            } else {
                Timber.e(it.error()!!)
            }
        })

        val inputs = listOf(et_seal, et_broken_seals, et_delivery_note)

        inputs.forEach { et ->
            et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        et.error = null
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        et.error = "This field can't be empty"
                    }
                }
            })
        }

        viewModel.getTruck().observe(this, Observer {
            if (it.isSuccessful) {
                initialTruckValues(it.data()!!)
            } else {
                Timber.e(it.error()!!)
            }
        })

    }

    private fun initialTruckValues(truck: TruckModel) {

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm aaa")
        tv_today_date.text = sdf.format(Date()).toUpperCase()


        tv_truck_id.text = truck.truckId
        // TODO: tv_depot_name.text =

        // driver data
        tv_driver_value.text = truck.drivername
        tv_driver_passport_value.text = truck.driverid
        tv_number_plate_value.text = truck.numberplate
        tv_organisation_value.text = truck.company?.name

        // inputs
        val data = truck.stagedata?.get("4")?.data
        et_delivery_note.setText(data?.deliveryNote)
        et_seal.setText(data?.seals?.range)
        val broken = data?.seals?.broken?.joinToString("-")
        et_broken_seals.setText(broken)


        // fuel
        tv_pms_value.text = truck.fuel?.pms?.qty.toString()
        tv_ago_value.text = truck.fuel?.ago?.qty.toString()
        tv_ik_value.text = truck.fuel?.ik?.qty.toString()

        // qr
        val depotUrl =
            "https://us-central1-emkaybeta.cloudfunctions.net/truckDetail?D=${_user.config?.depotdata?.depotid}&T=${truck.truckId}"

        val dimensions = imageUtil.dpToPx(this, 150)

        val thread = Thread(Runnable {
            val myBitmap = QRCode.from(depotUrl)
                .withSize(dimensions, dimensions)
                .bitmap()

            runOnUiThread {
                iv_qr.setImageBitmap(myBitmap)
            }
        })

        thread.start()
    }

}
