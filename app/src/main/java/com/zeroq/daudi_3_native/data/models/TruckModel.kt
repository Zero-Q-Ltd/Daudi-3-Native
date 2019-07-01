package com.zeroq.daudi_3_native.data.models


data class TruckModel(
    var Id: String?,
    var truckId: String?,
    var numberplate: String?,
    var stage: Int?,
    var isPrinted: String?,
    var orderref: String?,
    var drivername: String?,
    var driverid: String?,
    var frozen: Boolean?,
    var company: Company?,
    var compartments: List<Compartment>?,
    var orderdata: OrderData?,
    var config: TruckConfig?,
    var fuel: Fuel?,
    var stagedata: Map<String, Stage>?
) {
    constructor()
            : this(
        null, null, null,
        null, null, null,
        null, null, null,
        null, null, null,
        null, null, null
    )
}