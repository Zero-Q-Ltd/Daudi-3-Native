package com.zeroq.daudi_3_native.events

import com.zeroq.daudi_3_native.data.models.TruckModel

class QueueingEvent(val trucks: List<TruckModel>?, val error: Exception?)