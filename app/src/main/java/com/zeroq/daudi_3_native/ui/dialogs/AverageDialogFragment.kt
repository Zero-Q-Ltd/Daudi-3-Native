package com.zeroq.daudi_3_native.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.adapters.OmcSpinnerAdapter
import com.zeroq.daudi_3_native.data.models.OmcModel
import kotlinx.android.synthetic.main.fragment_average_dialog.*

class AverageDialogFragment(var omcs: List<OmcModel>) : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.fragment_average_dialog,
            container, false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    private fun initView() {
        val adapter = OmcSpinnerAdapter(activity!!.baseContext, R.layout.spinner_row, ArrayList(omcs))
        spinner.adapter = adapter
    }
}