package com.zeroq.daudi_3_native.ui.processing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.data.repository.AdminRepository
import com.zeroq.daudi_3_native.data.repository.DepotRepository
import com.zeroq.daudi_3_native.vo.Resource
import javax.inject.Inject
import androidx.lifecycle.MediatorLiveData
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.vo.CompletionLiveData


class ProcessingViewModel @Inject constructor(
    adminRepo: AdminRepository,
    var depotRepository: DepotRepository,
    firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _user: LiveData<Resource<UserModel>> = MutableLiveData()
    private val _userId = MutableLiveData<String>()

    private val _depotId = MutableLiveData<String>()


    init {
        _user = Transformations.switchMap(_userId, adminRepo::getAdmin)

        _userId.value = firebaseAuth.uid
    }

    fun getUser(): LiveData<Resource<UserModel>> {
        return _user
    }

    fun setDepoId(depotid: String) {
        if (depotid != _depotId.value) _depotId.value = depotid
    }

    fun updateExpire(truck: TruckModel, minutes: Long): CompletionLiveData {
        return depotRepository.updateProcessingExpire(_depotId.value!!, truck, minutes)
    }




}