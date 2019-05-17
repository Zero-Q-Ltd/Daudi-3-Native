package com.zeroq.daudi_3_native.data.models

data class User(
    var uid: String?, var email: String, var number: Number?, var displayName: String?,
    var phoneNumber: Boolean?, var login: Boolean?, var inDb: Boolean?, var photoURL: Boolean?,
    var dev: Boolean?, var config: Config?, var fcmtokens: FcmToken
)
