package com.bcafinance.fintara.utils

import org.json.JSONObject

fun parseFieldValidationErrors(errorMessage: String): Map<String, String>? {
    return try {
        val json = JSONObject(errorMessage)
        val dataObj = json.optJSONObject("data") ?: return null

        val errors = mutableMapOf<String, String>()
        val keys = dataObj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            errors[key] = dataObj.getString(key)
        }
        errors
    } catch (e: Exception) {
        null
    }
}