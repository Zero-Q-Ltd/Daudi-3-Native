package com.zeroq.daudi_3_native.events

import com.zeroq.daudi_3_native.data.models.TruckModel

data class RecyclerTruckEvent(var position: Int, var truck: TruckModel)