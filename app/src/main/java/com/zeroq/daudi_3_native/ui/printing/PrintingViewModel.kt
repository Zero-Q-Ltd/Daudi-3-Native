package com.zeroq.daudi_3_native.ui.printing

import androidx.lifecycle.ViewModel
import com.zeroq.daudi_3_native.data.repository.DepotRepository
import com.zeroq.daudi_3_native.vo.CompletionLiveData
import javax.inject.Inject

class PrintingViewModel @Inject constructor(var depotRepository: DepotRepository) : ViewModel() {

    fun setPrintedState(depotId: String, idTruck: String): CompletionLiveData {
        return depotRepository.updatePrintedState(depotId, idTruck)
    }

    fun completeOrder(depotId: String, idTruck: String): CompletionLiveData {
        return depotRepository.completeTruck(depotId, idTruck)
    }
}