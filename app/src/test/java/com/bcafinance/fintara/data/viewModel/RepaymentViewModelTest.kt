package com.bcafinance.fintara.data.viewModel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bcafinance.fintara.data.model.dto.loan.RepaymentsScheduleResponse
import com.bcafinance.fintara.data.repository.RepaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.kotlin.*
import org.mockito.Mockito.mockStatic
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class RepaymentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository: RepaymentRepository = mock()
    private lateinit var viewModel: RepaymentViewModel
    private val testDispatcher = StandardTestDispatcher()

    companion object {
        @JvmStatic
        @BeforeClass
        fun mockLog() {
            val logMock = mockStatic(Log::class.java)

            logMock.`when`<Int> { Log.e(any(), any<String>()) }.thenReturn(0)
            logMock.`when`<Int> { Log.d(any(), any<String>()) }.thenReturn(0)
            logMock.`when`<Int> { Log.i(any(), any<String>()) }.thenReturn(0)

            // Log.w() dengan String message
            logMock.`when`<Int> { Log.w(any(), any<String>()) }.thenReturn(0)
            // Log.w() dengan Throwable
            logMock.`when`<Int> { Log.w(any(), any<Throwable>()) }.thenReturn(0)

            logMock.`when`<Int> { Log.v(any(), any<String>()) }.thenReturn(0)
        }
    }


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RepaymentViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetch repayments should update live data`() = runTest {
        // Arrange
        val loanId = "loan123"
        val fakeData = listOf(
            RepaymentsScheduleResponse(
                id = "1",
                installmentNumber = 1,
                amountToPay = BigDecimal(100000),
                amountPaid = BigDecimal(50000),
                dueDate = "2024-06-01",
                isLate = false,
                penaltyAmount = BigDecimal(0),
                paidAt = null
            )
        )
        whenever(repository.getRepayments(loanId)).thenReturn(fakeData)

        // Act
        viewModel.fetchRepayments(loanId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        Assert.assertEquals(fakeData, viewModel.repayments.value)
    }

    @Test
    fun `fetch repayments should handle error gracefully`() = runTest {
        // Arrange
        val loanId = "loan123"
        whenever(repository.getRepayments(loanId)).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.fetchRepayments(loanId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        Assert.assertNull(viewModel.repayments.value)
    }
}
