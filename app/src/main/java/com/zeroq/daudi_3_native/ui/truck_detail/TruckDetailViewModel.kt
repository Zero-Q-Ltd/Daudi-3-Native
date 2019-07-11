package com.zeroq.daudi_3_native.ui.truck_detail

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.data.repository.AdminRepository
import com.zeroq.daudi_3_native.data.repository.DepotRepository
import com.zeroq.daudi_3_native.vo.Resource
import com.zeroq.daudi_3_native.vo.combineLatest
import javax.inject.Inject

class TruckDetailViewModel @Inject constructor(
    adminRepo: AdminRepository,
    depotRepository: DepotRepository,
    firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _user: LiveData<Resource<UserModel>> = MutableLiveData()
    private var _truck: LiveData<Resource<TruckModel>> = MutableLiveData()

    private val _userId = MutableLiveData<String>()
    private val _truckId = MutableLiveData<String>()
    private var _depotId = MutableLiveData<String>()

    private var _combinedDepoTruckId = MutableLiveData<Pair<String, String>>()


    init {
        _user = Transformations.switchMap(_userId, adminRepo::getAdmin)

        _depotId.combineLatest(_truckId).observeForever(Observer {
            _combinedDepoTruckId.value = it
        })

        _truck = Transformations.switchMap(_combinedDepoTruckId, depotRepository::getTruck)

        _userId.value = firebaseAuth.uid
    }

    fun setTruckId(truckId: String) {
        if (truckId != _truckId.value) _truckId.value = truckId
    }

    fun setDepotId(depotId: String) {
        if (depotId != _depotId.value) _depotId.value = depotId
    }

    fun getUser(): LiveData<Resource<UserModel>> {
        return _user
    }

    fun getTruck(): LiveData<Resource<TruckModel>> {
        return _truck
    }

}