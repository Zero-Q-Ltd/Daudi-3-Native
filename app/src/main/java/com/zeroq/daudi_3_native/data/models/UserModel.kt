package com.zeroq.daudi_3_native.data.models

data class UserModel(
    var Active: Boolean?, var Id: String?,
    var config: Config?, var data: UserData?, var dev: Boolean?,
    var email: String?, var fcmtokens: FcmToken?,
    var status: UserStatus?
) : Model() {
    constructor() : this(
        null, null, null, null,
        null, null, null, null
    )
}