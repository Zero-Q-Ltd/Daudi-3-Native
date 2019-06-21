package com.zeroq.daudi_3_native.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.data.repository.AdminRepository
import com.zeroq.daudi_3_native.vo.Resource
import javax.inject.Inject

class UserViewModel @Inject constructor(var adminRepo: AdminRepository) : ViewModel() {

    //    var user: MutableLiveData<UserModel> = MutableLiveData()
    var user: LiveData<Resource<UserModel>> = MutableLiveData()
    private val id: MutableLiveData<String> = MutableLiveData()

    init {
        user = Transformations.switchMap(id, adminRepo::getAdmin)
    }
//
//    fun getUser(userId: String): LiveData<UserModel> {
//
//        val authUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
//
//        fireStoreRepo.getUser(userId).addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
//            if (e != null) {
//                Timber.e(e)
//                user.value = null
//            }
//
//            if (snapshot != null && snapshot.exists()) {
//                user.value = snapshot.toObject(UserModel::class.java)
//            } else {
//                user.value = null
//                Timber.d("Current data: null")
//            }
//        })
//        return user
//    }

    fun setAdminId(id: String): UserViewModel {
        this.id.value = id
        return this
    }

    fun getAdmin(): LiveData<Resource<UserModel>> {
        return user
    }


}