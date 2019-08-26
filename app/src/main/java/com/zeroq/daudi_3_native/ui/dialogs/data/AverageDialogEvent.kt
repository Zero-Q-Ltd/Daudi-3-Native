package com.zeroq.daudi_3_native.ui.dialogs.data

import com.zeroq.daudi_3_native.data.models.OmcModel

data class AverageDialogEvent(
    var pms: Double?, var ago: Double?,
    var ik: Double?,
    var omc: OmcModel?
) {
    constructor() : this(null, null, null, null)
}