package com.bcafinance.fintara.data.model

data class ApiResponse<T>(
    val timestamp: String?,
    val status: Int,
    val message: Any, // Bisa String atau List<String>
    val data: T?
) {
    fun getFormattedMessages(): String {
        return when (message) {
            is String -> message
            is List<*> -> (message as List<*>).joinToString("\n") { "- $it" }
            else -> "Terjadi kesalahan"
        }
    }
}


