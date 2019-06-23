package com.zeroq.daudi_3_native.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.data.repository.AdminRepository
import com.zeroq.daudi_3_native.vo.Resource
import javax.inject.Inject

class MainViewModel @Inject constructor(adminRepo: AdminRepository) : ViewModel() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var _user: LiveData<Resource<UserModel>> = MutableLiveData()
    private val _userId = MutableLiveData<String>()
    private var _depotId = MutableLiveData<String>()

    init {
        _user = Transformations.switchMap(_userId, adminRepo::getAdmin)

        // init fetching of admin data
        _userId.value = firebaseAuth.uid
    }

    fun setDeportId(depotId: String) {
        if (_depotId.value != depotId) {
            _depotId.value = depotId
        }
    }
}