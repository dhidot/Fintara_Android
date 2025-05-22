package com.bcafinance.fintara.data.model.dto.customer

data class FirstTimeUpdateRequest(
    val jenisKelamin: String,
    val ttl: String,
    val alamat: String,
    val noTelp: String,
    val nik: String,
    val namaIbuKandung: String,
    val pekerjaan: String,
    val gaji: Double,
    val noRek: String,
    val statusRumah: String
)