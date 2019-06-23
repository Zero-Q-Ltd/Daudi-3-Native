package com.zeroq.daudi_3_native.data.repository

import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject
import javax.inject.Named

class DepotRepository
@Inject constructor(@Named("depots") val depots: CollectionReference) {

    fun getAllTrucks() {

    }

}