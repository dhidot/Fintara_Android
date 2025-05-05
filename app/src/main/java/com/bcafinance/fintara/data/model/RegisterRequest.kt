package com.bcafinance.fintara.data.model

data class RegisterRequest(
	val name: String,
	val email: String,
	val jenisKelamin: String, // "LAKI_LAKI" atau "PEREMPUAN"
	val password: String
)

