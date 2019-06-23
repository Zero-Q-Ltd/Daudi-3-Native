package com.zeroq.daudi_3_native.ui.processing


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.commons.BaseFragment

class ProcessingFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

}
