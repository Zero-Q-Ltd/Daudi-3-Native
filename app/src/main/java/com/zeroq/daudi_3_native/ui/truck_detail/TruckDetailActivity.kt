package com.zeroq.daudi_3_native.ui.truck_detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.Batches
import com.zeroq.daudi_3_native.data.models.TruckModel
import kotlinx.android.synthetic.main.activity_truck_detail.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TruckDetailActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var truckDetailViewModel: TruckDetailViewModel

    private val _fuelTypeList = ArrayList<String>()


    // set data to compartments
    private lateinit var viewComp: List<EditText>

    private lateinit var btnComp: List<AppCompatButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_detail)


        viewComp = listOf(
            et_c1_qty, et_c2_qty,
            et_c3_qty, et_c4_qty, et_c5_qty, et_c6_qty, et_c7_qty
        )

        btnComp = listOf(
            et_c1_type, et_c2_type,
            et_c3_type, et_c4_type, et_c5_type, et_c6_type, et_c7_type
        )

        /*
        * set  the viewModel
        * */
        truckDetailViewModel = getViewModel(TruckDetailViewModel::class.java)
        truckDetailViewModel.setTruckId(intent.getStringExtra("TRUCK_ID"))

        truckDetailViewModel.getUser().observe(this, Observer {
            if (it.isSuccessful) {
                val user = it.data()
                truckDetailViewModel.setDepotId(user?.config?.depotdata?.depotid!!)
            } else {
                Timber.e(it.error()!!)
            }
        })


        truckDetailViewModel.getTruck().observe(this, Observer {
            if (it.isSuccessful) {
                initialTruckValues(it.data()!!)
            } else {
                Timber.e(it.error()!!)
            }
        })




        initToolbar()
        compartimentsButtonOps()
        compartmentsInputs()

    }

    private fun initialTruckValues(truck: TruckModel) {

        _fuelTypeList.clear()
        _fuelTypeList.add("EMPTY")

        if (truck.fuel?.pms?.qty != 0) _fuelTypeList.add("PMS")
        if (truck.fuel?.ago?.qty != 0) _fuelTypeList.add("AGO")
        if (truck.fuel?.ik?.qty != 0) _fuelTypeList.add("IK")

        tv_truck_id.text = truck.truckId
        tv_customer_value.text = truck.company?.name
        et_driver_name.setText(truck.drivername)
        et_driver_id.setText(truck.driverid)
        et_driver_plate.setText(truck.numberplate)

        // fuel
        tv_pms.text = "PMS [ " + truck.fuel?.pms?.qty + " ]"
        tv_ago.text = "AGO [ " + truck.fuel?.ago?.qty + " ]"
        tv_ik.text = "IK      [ " + truck.fuel?.ik?.qty + " ]"


        // fuel entries

        tv_pms_entry.text = getBatchName(truck.fuel?.pms!!)
        tv_ago_entry.text = getBatchName(truck.fuel?.ago!!)
        tv_ik_entry.text = getBatchName(truck.fuel?.ik!!)




        truck.compartments?.forEachIndexed { index, compartment ->
            if (compartment.qty != null && compartment.qty != 0) {
                btnComp[index].text = compartment.fueltype?.toUpperCase()
                viewComp[index].setText(compartment.qty!!.toString())
            } else {
                btnComp[index].text = "EMPTY"
                viewComp[index].text = null
                viewComp[index].hint = "Empty Comp"
                viewComp[index].isEnabled = false
            }
        }

        tv_auth_by_value.text = truck.stagedata!!["1"]?.user?.name

        // display name
        tv_confirmed_by_value.text = firebaseAuth.currentUser?.displayName


        /**
         * current data, will change it later to be regenerated before printing
         * */
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm aaa")
        tv_today_date.text = sdf.format(Date()).toUpperCase()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }


    private fun compartimentsButtonOps() {
        /**
         * make sure the compartment values tally with the given fuel
         * */

        btnComp.forEachIndexed { i, btn ->
            btn.setOnClickListener {
                var index = _fuelTypeList.indexOf(btn.text)
                index++


                if (index > (_fuelTypeList.size - 1)) {
                    index = 0

                    viewComp[i].text = null
                    viewComp[i].hint = "Empty Comp"
                    viewComp[i].error = null

                    /**
                     * disable
                     * */
                    viewComp[i].isEnabled = false
                } else {
                    viewComp[i].hint = "Enter Amount"
                    viewComp[i].isEnabled = true
                }

                btn.text = _fuelTypeList[index]
            }
        }
    }

    private fun compartmentsInputs() {

        viewComp.forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (it.isEnabled && (s.isNullOrEmpty() || s.toString().toInt() < 500)) {
                        it.error = "Should contain more than 500L"
                    } else {
                        if (it.isEnabled && (s.isNullOrEmpty() || s.toString().toInt() > 30000)) {
                            it.error = "Can't carry more than 30000L"
                        } else {
                            it.error = null
                        }
                    }
                }
            })
        }
    }


    private fun getBatchName(batches: Batches): String? {
        val temp = if (batches.batches!!["1"]?.qty != 0) {
            batches.batches!!["1"]?.Name
        } else {
            batches.batches!!["0"]?.Name
        }

        return temp ?: "****************"
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }
}
