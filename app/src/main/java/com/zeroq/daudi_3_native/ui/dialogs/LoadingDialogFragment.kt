package com.zeroq.daudi_3_native.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.ui.dialogs.data.LoadingDialogEvent
import io.reactivex.subjects.PublishSubject

class LoadingDialogFragment(var truck: TruckModel) : DialogFragment() {
    var loadingEvent =
        PublishSubject.create<LoadingDialogEvent>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.fragment_loading_dialog,
            container,
            false
        )
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewInit()
    }


    private fun viewInit() {

    }
}