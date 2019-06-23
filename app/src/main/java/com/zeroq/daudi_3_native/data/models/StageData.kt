package com.zeroq.daudi_3_native.data.models

import java.util.*

data class StageData(
    var `0`: SData?,
    var `1`: SData?,
    var `2`: SData?,
    var `3`: SData?,
    var `4`: SData4?

) {
    constructor() : this(null, null, null, null, null)
}


data class _Data4(
    var deliveryNote: String?,
    var seals: Seals?
) {
    constructor() : this(null, null)
}

data class SData(
    var user: _User?,
    var data: _Data?
) {
    constructor() : this(null, null)
}

data class SData4(
    var user: _User?,
    var data: _Data4?
) {
    constructor() : this(null, null)
}

data class _Data(
    var expiry: List<Expiry>?
) {
    constructor() : this(null)
}


data class Expiry(
    var time: String?,
    var timestamp: Date?
) {
    constructor() : this(null, null)
}

data class _User(
    var name: String?,
    var time: Number?,
    var uuid: Number?
) {
    constructor() : this(null, null, null)
}


data class Seals(
    var range: String?,
    var broken: String?
) {
    constructor() : this(null, null)
}
