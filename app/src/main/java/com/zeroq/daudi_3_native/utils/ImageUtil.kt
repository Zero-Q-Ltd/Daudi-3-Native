package com.zeroq.daudi_3_native.utils

import android.content.Context
import javax.inject.Inject
import android.util.TypedValue
import kotlin.math.roundToInt


class ImageUtil @Inject constructor() {

    fun dpToPx(context: Context, dp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).roundToInt()
    }
}