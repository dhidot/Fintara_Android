package com.bcafinance.fintara.data.model.dto

import java.io.Serializable

data class FirstTimeUpdateRequest(
    val ttl: String,
    val alamat: String,
    val noTelp: String,
    val nik: String,
    val namaIbuKandung: String,
    val pekerjaan: String,
    val gaji: Double,
    val noRek: String,
    val statusRumah: String,
    val ktpPhotoUrl: String?,
    val selfiePhotoUrl: String?
) : Serializable
