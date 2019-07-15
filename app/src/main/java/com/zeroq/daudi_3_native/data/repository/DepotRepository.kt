package com.zeroq.daudi_3_native.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zeroq.daudi_3_native.data.models.Compartment
import com.zeroq.daudi_3_native.data.models.Expiry
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.utils.MyTimeUtils
import com.zeroq.daudi_3_native.vo.CompletionLiveData
import com.zeroq.daudi_3_native.vo.DocumentLiveData
import com.zeroq.daudi_3_native.vo.QueryLiveData
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.math.min

class DepotRepository
@Inject constructor(@Named("depots") val depots: CollectionReference, val firestore: FirebaseFirestore) {


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

    fun getTruck(combineDepoTruckId: Pair<String, String>): DocumentLiveData<TruckModel> {
        val truckRef =
            depots.document(combineDepoTruckId.first)
                .collection("trucks")
                .document(combineDepoTruckId.second)

        val data: DocumentLiveData<TruckModel> = DocumentLiveData(truckRef, TruckModel::class.java)
        truckRef.addSnapshotListener(data)

        return data
    }

    /**
     * set processing expire
     * **/
    fun updateProcessingExpire(depotId: String, truck: TruckModel, minutes: Long):
            CompletionLiveData {

        val completion = CompletionLiveData()
        updateProcessingExpireTask(depotId, truck, minutes).addOnCompleteListener(completion)
        return completion

    }

    private fun updateProcessingExpireTask(depotId: String, t: TruckModel, minutes: Long): Task<Void> {
        val truckRef = depots
            .document(depotId)
            .collection("trucks").document(t.Id!!)

        return firestore.runTransaction { transition ->
            val truck: TruckModel? = transition.get(truckRef).toObject(TruckModel::class.java)

            // add new time
            val startDate = Calendar.getInstance().time

            val exTime: String = MyTimeUtils.formatElapsedTime(TimeUnit.MINUTES.toMillis(minutes))

            val calendar = Calendar.getInstance()
            calendar.time = startDate
            calendar.add(Calendar.MINUTE, minutes.toInt())

            val expireDate = calendar.time

            /**
             * modify the truck object
             * */
            val expireObj = Expiry(startDate, exTime, expireDate)

            val exp: ArrayList<Expiry>? = truck?.stagedata!!["1"]?.data?.expiry
            exp?.add(0, expireObj)

            // commit to fireStore
            transition.update(truckRef, "stagedata.1.data.expiry", exp)

            return@runTransaction null
        }
    }


    /**
     * logic to update compartments
     * */
    fun updateCompartmentAndDriver(
        depotId: String,
        idTruck: String,
        compartmentList: List<Compartment>,
        driverId: String, driverName: String, numberPlate: String
    ): CompletionLiveData {
        val completion = CompletionLiveData()
        updateCompartmentAndDriverTask(depotId, idTruck, compartmentList, driverId, driverName, numberPlate)
            .addOnCompleteListener(completion)

        return completion
    }

    private fun updateCompartmentAndDriverTask(
        depotId: String,
        idTruck: String,
        compartmentList: List<Compartment>,
        driverId: String, driverName: String, numberPlate: String
    ): Task<Void> {
        val truckRef = depots
            .document(depotId)
            .collection("trucks").document(idTruck)

        return firestore.runTransaction { transition ->
            val truck: TruckModel? = transition.get(truckRef).toObject(TruckModel::class.java)

            /*
            * driver details
            * */
            transition.update(truckRef, "driverid", driverId)
            transition.update(truckRef, "drivername", driverName)
            transition.update(truckRef, "numberplate", numberPlate)

            /*
            * update compartment array
            * */
            transition.update(truckRef, "compartments", compartmentList)

            return@runTransaction null
        }
    }

}