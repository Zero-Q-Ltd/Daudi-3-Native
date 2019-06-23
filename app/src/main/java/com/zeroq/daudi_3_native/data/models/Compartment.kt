package com.zeroq.daudi_3_native.data.models

data class Compartment(
    var fueltype: String?,
    var qty: Number?
) {
    constructor() : this(null, null)
}