package com.zeroq.daudi_3_native.data.models

data class Company(
    var Id: String?,
    var contactname: String?,
    var name: String?,
    var phone: String?

) {
    constructor() : this(
        null, null, null, null
    )
}