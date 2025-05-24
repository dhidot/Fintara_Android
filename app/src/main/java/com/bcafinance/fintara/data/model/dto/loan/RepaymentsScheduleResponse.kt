package com.bcafinance.fintara.data.model.dto.loan

import java.math.BigDecimal
import java.time.LocalDate

data class RepaymentsScheduleResponse (
    val id: String,
    val installmentNumber: Int,
    val amountToPay: BigDecimal,
    val amountPaid: BigDecimal,
    val dueDate: LocalDate,
    val isLate: Boolean,
    val penaltyAmount: BigDecimal,
    val paidAt: LocalDate?
)