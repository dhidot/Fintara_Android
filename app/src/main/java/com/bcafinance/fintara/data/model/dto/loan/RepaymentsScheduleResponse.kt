package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal
import java.time.LocalDate

data class RepaymentsScheduleResponse (
    val id: String,
    val installmentNumber: Int,
    val amountToPay: BigDecimal,
    val amountPaid: BigDecimal,
    val dueDate: String,  // change to String
    val isLate: Boolean,
    val penaltyAmount: BigDecimal,
    val paidAt: String?   // change to String?
)
