package com.zeroq.daudi_3_native.ui.truck_detail

import android.os.Bundle
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*

class TruckDetailActivity : BaseActivity() {

    lateinit var truckDetailViewModel: TruckDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truck_detail)

        /*
        * set  the viewModel
        * */
        truckDetailViewModel = getViewModel(TruckDetailViewModel::class.java)
        truckDetailViewModel.setTruckId(intent.getStringExtra("TRUCK_ID"))



        initToolbar()
        initialValues()
        compartimentsOps()
    }

    private fun initialValues() {

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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }
}
