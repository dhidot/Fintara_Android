package com.bcafinance.fintara.utils

import org.json.JSONObject

fun parseErrorMessage(errorMessage: String?): String {
    if (errorMessage.isNullOrEmpty()) {
        return "An unknown error occurred."
    }

    return try {
        val jsonPattern = """\{.*\}""".toRegex()
        val matchResult = jsonPattern.find(errorMessage)

        if (matchResult != null) {
            val errorResponse = JSONObject(matchResult.value)
            errorResponse.optString("message", "An error occurred.")
        } else {
            errorMessage
        }
    } catch (e: Exception) {
        "An error occurred. Please try again."
    }
}

fun parseFieldValidationErrors(errorMessage: String?): Map<String, String>? {
    if (errorMessage == null || !errorMessage.contains("Validasi gagal:")) return null

    return try {
        val errorPart = errorMessage.substringAfter("Validasi gagal:").trim()
            .removePrefix("{").removeSuffix("}")

        errorPart.split(",").associate {
            val (field, msg) = it.split("=").map(String::trim)
            field to msg
        }
    } catch (e: Exception) {
        null
    }
}
