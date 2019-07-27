package com.zeroq.daudi_3_native.ui.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.zeroq.daudi_3_native.R
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.ui.dialogs.data.LoadingDialogEvent
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_loading_dialog.*
import kotlin.math.abs
import kotlin.math.floor

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
        tv_loading_truck_id.text = "Truck ${truck.truckId}"

        /**
         * cancel dialog
         * */
        tv_loading_cancel.setOnClickListener {
            dismiss()
        }

        val actuals: List<Pair<EditText, Int>> = listOf(
            Pair(et_pms_actual, truck.fuel?.pms?.qty!!),
            Pair(et_ago_actual, truck.fuel?.ago?.qty!!), Pair(et_ik_actual, truck.fuel?.ik?.qty!!)
        )

        /**
         * may improve this code later
         * */

        actuals.forEach {
            hideView(it.first, it.second)

            val allowedActual = 0.9




            if (it.second > 0) {
                it.first.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {


                        if (!s.isNullOrEmpty()) {
                            val minActual = abs(allowedActual * it.second).toInt()
                            val providedActual = s.toString().toInt()

                            if ((minActual < providedActual) && (providedActual < it.second)) {
                                it.first.error = null
                            }
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!s.isNullOrEmpty()) {
                            val minActual = abs(allowedActual * it.second).toInt()
                            val providedActual = s.toString().toInt()

                            if (providedActual < minActual) {
                                it.first.error =
                                    "Amount should be between $minActual and ${it.second}"
                            }

                            if (providedActual > it.second) {
                                it.first.error = "Amount cant be more than ${it.second}"
                            }
                        }
                    }
                })
            }

            /**
             *
             * */
        }


    }

    private fun hideView(view: EditText, quantity: Int) {
        if (quantity <= 0) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}