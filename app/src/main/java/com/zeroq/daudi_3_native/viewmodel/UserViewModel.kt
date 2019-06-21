package com.zeroq.daudi_3_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.data.repository.FirestoreRepository
import timber.log.Timber
import javax.inject.Inject

class UserViewModel @Inject constructor(var fireStoreRepo: FirestoreRepository) : ViewModel() {

    var user: MutableLiveData<UserModel> = MutableLiveData()

    fun getUser(userId: String): LiveData<UserModel> {
        fireStoreRepo.getUser(userId).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Timber.e(e)
                user.value = null
            }

            if (snapshot != null && snapshot.exists()) {
                Timber.d("Current data: ${snapshot.data}")
                user.value = snapshot.toObject(UserModel::class.java)
            } else {
                user.value = null
                Timber.d("Current data: null")
            }
        })
        return user;
    }


}