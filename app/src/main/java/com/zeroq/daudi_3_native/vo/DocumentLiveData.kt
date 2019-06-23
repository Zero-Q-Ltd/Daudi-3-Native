package com.zeroq.daudi_3_native.vo

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*


class DocumentLiveData<T>(private val ref: DocumentReference, private val type: Class<T>) : LiveData<Resource<T>>(),
    EventListener<DocumentSnapshot> {
    private var registration: ListenerRegistration? = null

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            setValue(Resource(e))
            return
        }
        setValue(Resource(snapshot!!.toObject(type) as T))
    }

    override fun onActive() {
        super.onActive()
        registration = ref.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        if (registration != null) {
            registration!!.remove()
            registration = null
        }
    }
}
