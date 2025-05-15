package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal

class LoanRequest {
    var amount: BigDecimal? = null
    var tenor: Int? = null
    var latitude: Double? = null
    var longitude: Double? = null
}