package com.zeroq.daudi_3_native.ui.truck_detail

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.zeroq.daudi_3_native.data.models.Compartment
import com.zeroq.daudi_3_native.data.models.DepotModel
import com.zeroq.daudi_3_native.data.models.TruckModel
import com.zeroq.daudi_3_native.data.models.UserModel
import com.zeroq.daudi_3_native.data.repository.AdminRepository
import com.zeroq.daudi_3_native.data.repository.DepotRepository
import com.zeroq.daudi_3_native.vo.CompletionLiveData
import com.zeroq.daudi_3_native.vo.Resource
import com.zeroq.daudi_3_native.vo.combineLatest
import javax.inject.Inject

class TruckDetailViewModel @Inject constructor(
    adminRepo: AdminRepository,
    var depotRepository: DepotRepository,
    firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _user: LiveData<Resource<UserModel>> = MutableLiveData()
    private var _truck: LiveData<Resource<TruckModel>> = MutableLiveData()

    private val _userId = MutableLiveData<String>()
    private val _truckId = MutableLiveData<String>()
    private var _depotId = MutableLiveData<String>()

    private var _combinedDepoTruckId = MutableLiveData<Pair<String, String>>()
    private var _depo: LiveData<Resource<DepotModel>> = MutableLiveData()


    init {
        _user = Transformations.switchMap(_userId, adminRepo::getAdmin)

        _depotId.combineLatest(_truckId).observeForever(Observer {
            _combinedDepoTruckId.value = it
        })

        _truck = Transformations.switchMap(_combinedDepoTruckId, depotRepository::getTruck)
        _depo = Transformations.switchMap(_depotId, depotRepository::getDepot)

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

    fun getDepot(): LiveData<Resource<DepotModel>> {
        return _depo
    }

    fun updateTruckComAndDriver(
        depotId: String,
        idTruck: String,
        compartmentList: List<Compartment>,
        driverId: String, driverName: String, numberPlate: String
    ): CompletionLiveData {
        return depotRepository.updateCompartmentAndDriver(
            depotId, idTruck, compartmentList,
            driverId, driverName, numberPlate
        )
    }

}