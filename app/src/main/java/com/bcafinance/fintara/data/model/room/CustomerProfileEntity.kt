package com.bcafinance.fintara.data.model.room

import java.math.BigDecimal
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bcafinance.fintara.data.model.CustomerDetails
import com.bcafinance.fintara.data.model.Plafond
import com.bcafinance.fintara.data.model.dto.auth.UserWithCustomerResponse

@Entity(tableName = "customer_profile")
data class CustomerProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String,
    val jenisKelamin: String?,
    val ttl: String?,
    val alamat: String?,
    val noTelp: String?,
    val nik: String?,
    val namaIbuKandung: String?,
    val pekerjaan: String?,
    val gaji: BigDecimal?,
    val noRek: String?,
    val statusRumah: String?,
    val plafondId: String?,
    val plafondName: String?,
    val plafondMaxAmount: BigDecimal?,
    val plafondInterestRate: Double?,
    val plafondMinTenor: Int?,
    val plafondMaxTenor: Int?,
    val updatedAt: Long // Untuk mendeteksi perubahan dan menentukan apakah data perlu di-refresh
)

fun CustomerProfileEntity.toDto(): UserWithCustomerResponse {
    return UserWithCustomerResponse(
        id = this.id,
        name = this.name,
        email = this.email,
        role = this.role,
        customerDetails = CustomerDetails(
            id = this.id,
            jenisKelamin = this.jenisKelamin,
            ttl = this.ttl,
            alamat = this.alamat,
            noTelp = this.noTelp,
            nik = this.nik,
            namaIbuKandung = this.namaIbuKandung,
            pekerjaan = this.pekerjaan,
            gaji = this.gaji,
            noRek = this.noRek,
            statusRumah = this.statusRumah,
            plafond = Plafond(
                id = this.plafondId ?: "",
                name = this.plafondName ?: "",
                maxAmount = this.plafondMaxAmount ?: BigDecimal.ZERO,
                interestRate = this.plafondInterestRate ?: 0.0,
                minTenor = this.plafondMinTenor ?: 0,
                maxTenor = this.plafondMaxTenor ?: 0
            )
        )
    )
}

