package com.bcafinance.fintara.data.model.dto

data class RegisterRequest(
	val name: String,         // Nama
	val email: String,        // Email
	val password: String      // Password
)
