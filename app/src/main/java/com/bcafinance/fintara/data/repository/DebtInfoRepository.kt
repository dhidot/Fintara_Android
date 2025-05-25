package com.bcafinance.fintara.data.repository

import com.bcafinance.fintara.config.network.api.DebtApiService
import com.bcafinance.fintara.data.model.dto.customer.DebtInfoResponse

class DebtInfoRepository(private val apiService: DebtApiService) {

    suspend fun getDebtInfo(): DebtInfoResponse? {
        val response = apiService.getDebtInfo()
        return if (response.status == 200) {
            response.data
        } else {
            null
        }
    }
}
