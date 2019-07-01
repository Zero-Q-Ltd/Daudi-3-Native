package com.zeroq.daudi_3_native.data.models

import java.lang.reflect.Type

data class Batch(
    var Id: String?,
    var Name: String?,
    // String or Integer
    var observed: Any?,
    var qty: Int?
) {
    constructor() : this(null, null, null, null)
}