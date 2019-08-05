package com.zeroq.daudi_3_native.utils

import android.view.View
import android.widget.TextView
import javax.inject.Inject

class ActivityUtil @Inject constructor() {
    /***
     * This module will contain all most of the views automation
     * */

    fun showProgress(view: View, show: Boolean) {
        if (show) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    fun showTextViewState(view: TextView, show: Boolean, msg: String?, tvColor: Int?) {
        if (show) {
            view.setTextColor(tvColor!!)
            view.visibility = View.VISIBLE
            view.text = msg
        } else {
            view.visibility = View.GONE
        }
    }

}