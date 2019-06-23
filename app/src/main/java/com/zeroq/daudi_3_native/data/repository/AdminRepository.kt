package com.zeroq.daudi_3_native.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.vo.DocumentLiveData
import javax.inject.Inject
import javax.inject.Named

class AdminRepository @Inject
constructor(@Named("admins") val admins: CollectionReference) {


    fun getAdmin(id: String): DocumentLiveData<UserModel> {
        val adminRef = admins.document(id)
        val data = DocumentLiveData(adminRef, UserModel::class.java)
        adminRef.addSnapshotListener(data)

        return data
    }
}