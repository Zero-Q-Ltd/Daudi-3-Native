package com.zeroq.daudi_3_native.data.models

data class OmcModel(var license: String?, var name: String?) : Model() {
    constructor() : this(null, null)
}