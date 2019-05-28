package com.zeroq.daudi_3_native.data.models

class UserData(var email: String?, var name: String?, var photoURL: String?, var uid: String?) {
    constructor() : this(null, null, null, null)
}