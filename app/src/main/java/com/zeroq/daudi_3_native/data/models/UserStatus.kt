package com.zeroq.daudi_3_native.data.models

import com.google.firebase.Timestamp

data class UserStatus(var online: Boolean?, var time: Timestamp?) {
    constructor() : this(false, null)
}