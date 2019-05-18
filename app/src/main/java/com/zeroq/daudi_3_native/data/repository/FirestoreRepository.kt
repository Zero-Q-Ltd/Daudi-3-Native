package com.zeroq.daudi_3_native.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreRepository @Inject constructor() {
    private var fireStore = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    /**
     * All abt firestore user
     * */

    // get single user
    fun getUser(userId: String): DocumentReference {
        return fireStore.document("admins/${userId}")
    }


}