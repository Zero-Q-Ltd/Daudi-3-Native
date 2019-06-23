package com.zeroq.daudi_3_native.data.models

data class Batch(
    var Id: String?,
    var Name: String?,
    var observed: Number?,
    var qty: Number?
) {
    constructor() : this(null, null, null, null)
}