package com.zeroq.daudi_3_native.ui.loading_order

import android.os.Bundle
import android.view.WindowManager
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*

class LoadingOrderActivity : BaseActivity() {

    lateinit var viewModel: LoadingOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_order)

        /**
         * hide keyboad
         * */
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        viewModel = getViewModel(LoadingOrderViewModel::class.java)

        setUpToolbar()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "GatePass"
    }
}
