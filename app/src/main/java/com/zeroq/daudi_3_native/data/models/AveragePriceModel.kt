package com.zeroq.daudi_3_native.data.models

data class AveragePriceModel(
    var fueltytype: String?, var omcId: String?,
    var price: Double?, var user: AveragePriceUser?
) : Model() {
    constructor() : this(null, null, null, null)
}