package com.zeroq.daudi_3_native.data.models

data class Fuel(
    var ago: Batches?,
    var ik: Batches?,
    var pms: Batches?
) {
    constructor() : this(null, null, null)
}

data class Batches(
    var qty: Int?,
    var batches: Batchz?
) {
    constructor() : this(null, null)
}

data class Batchz(var `0`: Batch?, var `1`: Batch?) {
    constructor() : this(null, null)

}