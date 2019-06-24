package com.zeroq.daudi_3_native.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.vo.QueryLiveData
import javax.inject.Inject
import javax.inject.Named

class DepotRepository
@Inject constructor(@Named("depots") val depots: CollectionReference) {


    private fun trucksQuery(depotId: String): Query {
        return depots
            .document(depotId)
            .collection("trucks")
            .whereGreaterThan("stage", 0)
            .whereLessThan("stage", 4)
    }

    fun getAllTrucks(depotId: String): QueryLiveData<TruckModel> {
        return QueryLiveData(trucksQuery(depotId), TruckModel::class.java)
    }

}