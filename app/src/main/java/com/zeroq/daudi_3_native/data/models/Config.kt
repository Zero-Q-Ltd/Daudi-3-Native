package com.zeroq.daudi_3_native.data.models

data class Config(
    var level: String?,
    var depotdata: DepotData?,
    var viewsandbox: Boolean?
) {
    constructor() : this(null, null, false)
}