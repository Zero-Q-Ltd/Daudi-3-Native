package com.zeroq.daudi_3_native.ui.truck_detail

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import com.zeroq.daudi_3_native.data.models.Batches
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.data.models.UserModel
import kotlinx.android.synthetic.main.activity_truck_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TruckDetailActivity : BaseActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var truckDetailViewModel: TruckDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_detail)

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
        compartimentsOps()
    }

    private fun initialTruckValues(truck: TruckModel) {
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


        // set data to compartments
        val viewComp: List<EditText> = listOf(
            et_c1_qty, et_c2_qty,
            et_c3_qty, et_c4_qty, et_c5_qty, et_c6_qty, et_c7_qty
        )

        val btnComp: List<AppCompatButton> = listOf(
            et_c1_type, et_c2_type,
            et_c3_type, et_c4_type, et_c5_type, et_c6_type, et_c7_type
        )

        truck.compartments?.forEachIndexed { index, compartment ->
            if (compartment.qty != null && compartment.qty != 0) {
                btnComp[index].text = compartment.fueltype?.toUpperCase()
                viewComp[index].setText(compartment.qty!!.toString())
            } else {
                btnComp[index].text = "EMPTY"
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


    private fun compartimentsOps() {
        /**
         * make sure the compartment values tally with the given fuel
         * */
//        val c1Supscription =
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
