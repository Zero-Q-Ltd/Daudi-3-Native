package com.zeroq.daudi_3_native.data.models


data class TruckModel(
    var Id: String?,
    var truckId: String?,
    var numberplate: String?,
    var stage: Number?,
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
    var stagedata: StageData?
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