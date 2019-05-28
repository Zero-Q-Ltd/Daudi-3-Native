package com.zeroq.daudi_3_native.data.models

data class QbConfig(var QbId: String?, var companyid: String?, var sandbox: String?) {
    constructor() : this(null, null, null)
}