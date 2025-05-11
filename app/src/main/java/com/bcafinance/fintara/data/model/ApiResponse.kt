package com.bcafinance.fintara.data.model

data class ApiResponse<T>(
    val timestamp: String?,
    val status: Int,
    val message: Any?, // Bisa String atau List<String>
    val data: T?
) {
    fun getFormattedMessages(): String {
        return when (message) {
            is String -> message
            is List<*> -> (message as List<*>).joinToString("\n") { "- $it" }
            else -> "Terjadi kesalahan"
        }
    }

    companion object {
        fun <T> success(data: T, message: Any? = null): ApiResponse<T> {
            return ApiResponse(
                timestamp = System.currentTimeMillis().toString(),
                status = 200,
                message = message,
                data = data
            )
        }

        // Static factory method untuk respons error
        fun <T> error(message: Any?, data: T? = null): ApiResponse<T> {
            return ApiResponse(
                timestamp = System.currentTimeMillis().toString(),
                status = 400,
                message = message,
                data = data
            )
        }
    }
}


