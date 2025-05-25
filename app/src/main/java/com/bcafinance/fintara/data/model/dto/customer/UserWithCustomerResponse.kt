package com.bcafinance.fintara.data.model.dto.customer


import android.os.Build
import androidx.annotation.RequiresApi
import com.bcafinance.fintara.data.model.CustomerDetails
import com.bcafinance.fintara.data.model.room.CustomerProfileEntity
import com.bcafinance.fintara.utils.parseBackendDateTimeToEpochMillis
import java.math.BigDecimal

data class UserWithCustomerResponse(
    val id: String,
    val name: String,
    val email: String,
    val fotoUrl: String,
    val role: String,
    val customerDetails: CustomerDetails?
)

@RequiresApi(Build.VERSION_CODES.O)
fun UserWithCustomerResponse.toEntity(): CustomerProfileEntity {
    return CustomerProfileEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        fotoUrl = this.fotoUrl,
        role = this.role,
        jenisKelamin = this.customerDetails?.jenisKelamin ?: "-", // default value jika null
        ttl = this.customerDetails?.ttl ?: "-", // default value jika null
        alamat = this.customerDetails?.alamat ?: "-", // default value jika null
        noTelp = this.customerDetails?.noTelp ?: "-", // default value jika null
        nik = this.customerDetails?.nik ?: "-", // default value jika null
        namaIbuKandung = this.customerDetails?.namaIbuKandung ?: "-", // default value jika null
        pekerjaan = this.customerDetails?.pekerjaan ?: "-", // default value jika null
        gaji = this.customerDetails?.gaji ?: BigDecimal.ZERO, // default value jika null
        noRek = this.customerDetails?.noRek ?: "-", // default value jika null
        statusRumah = this.customerDetails?.statusRumah ?: "-", // default value jika null
        ktpUrl = this.customerDetails?.ktpUrl ?: "-", // default value jika null
        selfieKtpUrl = this.customerDetails?.selfieKtpUrl ?: "-", // default value jika null
        remainingPlafond = this.customerDetails?.remainingPlafond ?: BigDecimal.ZERO, // default value jika null
        plafondId = this.customerDetails?.plafond?.id ?: "-", // default value jika null
        plafondName = this.customerDetails?.plafond?.name ?: "-", // default value jika null
        plafondMaxAmount = this.customerDetails?.plafond?.maxAmount ?: BigDecimal.ZERO, // default value jika null
        plafondInterestRate = this.customerDetails?.plafond?.interestRate ?: 0.0, // default value jika null
        plafondMinTenor = this.customerDetails?.plafond?.minTenor ?: 0, // default value jika null
        plafondMaxTenor = this.customerDetails?.plafond?.maxTenor ?: 0, // default value jika null
        updatedAt = this.customerDetails?.updatedAt?.let { parseBackendDateTimeToEpochMillis(it) } ?: 0L
    )
}



