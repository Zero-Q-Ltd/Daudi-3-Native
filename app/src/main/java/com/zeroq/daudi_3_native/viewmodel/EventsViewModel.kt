package com.zeroq.daudi_3_native.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zeroq.daudi_3_native.data.models.TruckModel
import javax.inject.Inject

class EventsViewModel @Inject constructor() : ViewModel() {
    val processingEvent = MutableLiveData<TruckModel>()
    val queueingEvent = MutableLiveData<TruckModel>()
    val loadingEvent = MutableLiveData<TruckModel>()

    fun setProcessingEvent(truckModel: TruckModel) {
        processingEvent.postValue(truckModel)
    }

    fun setQueueingEvent(truckModel: TruckModel) {
        queueingEvent.value = truckModel
    }

    fun setLoadingEvent(truckModel: TruckModel) {
        loadingEvent.value = truckModel
    }
}