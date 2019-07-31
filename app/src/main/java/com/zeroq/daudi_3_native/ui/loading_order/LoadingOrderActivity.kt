package com.zeroq.daudi_3_native.ui.loading_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity

class LoadingOrderActivity : BaseActivity() {

    lateinit var viewModel: LoadingOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_order)
        viewModel = getViewModel(LoadingOrderViewModel::class.java)
    }
}
