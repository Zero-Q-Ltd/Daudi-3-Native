package com.zeroq.daudi_3_native.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.zeroq.daudi_3_native.data.models.OmcModel
import com.zeroq.daudi_3_native.vo.QueryLiveData
import javax.inject.Inject
import javax.inject.Named

class OmcRepository @Inject constructor(
    @Named("omc") val omc: CollectionReference,
    val firestore: FirebaseFirestore
) {


    fun getAllOmcs(): QueryLiveData<OmcModel> {
        return QueryLiveData(omc, OmcModel::class.java)
    }



}