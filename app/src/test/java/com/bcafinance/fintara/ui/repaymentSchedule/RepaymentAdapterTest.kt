package com.bcafinance.fintara.ui.repaymentSchedule

import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import com.bcafinance.fintara.ui.repaymentSchedule.RepaymentAdapter
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class RepaymentAdapterTest {

    private val diffCallback = RepaymentAdapter.DiffCallback()

    private fun createItem(id: String, paidAmount: BigDecimal = BigDecimal.ZERO) = RepaymentsScheduleResponse(
        id = id,
        installmentNumber = 1,
        amountToPay = BigDecimal(100000),
        amountPaid = paidAmount,
        dueDate = "2024-06-01",
        isLate = false,
        penaltyAmount = BigDecimal(0),
        paidAt = null
    )

    @Test
    fun `areItemsTheSame returns true when ids match`() {
        val item1 = createItem("1")
        val item2 = createItem("1")

        assertTrue(diffCallback.areItemsTheSame(item1, item2))
    }

    @Test
    fun `areItemsTheSame returns false when ids differ`() {
        val item1 = createItem("1")
        val item2 = createItem("2")

        assertFalse(diffCallback.areItemsTheSame(item1, item2))
    }

    @Test
    fun `areContentsTheSame returns true when all fields match`() {
        val item1 = createItem("1")
        val item2 = createItem("1")

        assertTrue(diffCallback.areContentsTheSame(item1, item2))
    }

    @Test
    fun `areContentsTheSame returns false when fields differ`() {
        val item1 = createItem("1", BigDecimal(1000))
        val item2 = createItem("1", BigDecimal(2000))

        assertFalse(diffCallback.areContentsTheSame(item1, item2))
    }
}
