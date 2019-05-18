package com.zeroq.daudi_3_native.data.models

//data class User(
//    var uid: String?, var email: String?, var number: String?, var displayName: String?,
//    var phoneNumber: Boolean?, var login: Boolean?, var inDb: Boolean?, var photoURL: Boolean?,
//    var dev: Boolean?, var config: Config?, var fcmtokens: FcmToken?
//) {
//    constructor() :
//            this(
//                null, null, null, null, null,
//                null, null, null, null, null, null
//            )
//}


data class User(
    var Active: Boolean?, var Id: String?, var QbId: String?,
    var config: Config?, var data: UserData?, var dev: Boolean?,
    var email: String?, var fcmtokens: FcmToken?, var profiledata: ProfileData?,
    var qbconfig: QbConfig?, var status: UserStatus?
) {
    constructor() : this(
        null, null, null, null,
        null, null, null, null, null, null, null
    )
}