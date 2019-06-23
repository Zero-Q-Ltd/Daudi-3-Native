package com.zeroq.daudi_3_native.data.models

data class OrderData(
    var OrderID: String?,
    var QbID: String?
) {
    constructor() : this(null, null)
}