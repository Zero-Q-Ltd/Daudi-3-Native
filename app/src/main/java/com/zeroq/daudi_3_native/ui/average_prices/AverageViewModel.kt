package com.zeroq.daudi_3_native.ui.average_prices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zeroq.daudi_3_native.data.models.OmcModel
import com.zeroq.daudi_3_native.data.repository.OmcRepository
import com.zeroq.daudi_3_native.vo.Resource
import javax.inject.Inject

class AverageViewModel @Inject constructor(var omcRepository: OmcRepository) : ViewModel() {

    private var omcsLive: LiveData<Resource<List<OmcModel>>> = MutableLiveData()

    init {
        omcsLive = omcRepository.getAllOmcs()
    }

    fun getOmcs(): LiveData<Resource<List<OmcModel>>> {
        return omcsLive
    }
}