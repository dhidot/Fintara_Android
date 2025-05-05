package com.bcafinance.fintara.data.model
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
	val name: String,         // Nama
	val email: String,        // Email

	@SerializedName("jenis_kelamin")
	val jenisKelamin: String, // Jenis Kelamin (PEREMPUAN, LAKI_LAKI)
	val password: String      // Password
)
