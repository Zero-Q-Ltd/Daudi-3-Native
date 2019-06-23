package com.zeroq.daudi_3_native.data.models

data class TruckConfig(
    var companyid: String?,
    var depot: Depot?
) {
    constructor() : this(null, null)
}