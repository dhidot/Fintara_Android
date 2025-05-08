package com.bcafinance.fintara.data.model

import java.math.BigDecimal

data class CustomerDetails(
    val id: String,
    val ttl: String?,
    val alamat: String?,
    val noTelp: String?,
    val nik: String?,
    val namaIbuKandung: String?,
    val pekerjaan: String?,
    val gaji: BigDecimal?,
    val noRek: String?,
    val statusRumah: String?,
    val plafond: Plafond?
)
