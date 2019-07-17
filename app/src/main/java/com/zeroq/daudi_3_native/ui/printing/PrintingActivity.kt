package com.zeroq.daudi_3_native.ui.printing

import android.os.Bundle
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseActivity

class PrintingActivity : BaseActivity() {

    lateinit var printingViewModel: PrintingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printing)

        printingViewModel = getViewModel(PrintingViewModel::class.java)
    }
}
