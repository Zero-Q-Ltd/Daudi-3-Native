package com.zeroq.daudi_3_native.vo


import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import timber.log.Timber

import java.util.ArrayList


class QueryLiveData<T>(private val query: Query, private val type: Class<T>) : LiveData<Resource<List<T>>>(),
    EventListener<QuerySnapshot> {
    private var registration: ListenerRegistration? = null

    override fun onEvent(snapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            setValue(Resource(e))
            return
        }
        setValue(Resource(documentToList(snapshots!!)))
    }


    override fun onActive() {
        super.onActive()
        registration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        if (registration != null) {
            registration!!.remove()
            registration = null
        }
    }

    @NonNull
    private fun documentToList(snapshots: QuerySnapshot): List<T> {
        val retList = ArrayList<T>()
        if (snapshots.isEmpty) {
            return retList
        }

        for (document in snapshots.documents) {
            retList.add(document.toObject(type)!!)
        }

        return retList
    }
}
