package com.bcafinance.fintara.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun saveUserName(name: String) {
        prefs.edit().putString("name", name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString("name", null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove("token")
        editor.remove("username") // dan data lainnya yang relevan
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun saveFirstLoginStatus(isFirstLogin: Boolean) {
        prefs.edit().putBoolean("first_login", isFirstLogin).apply()
    }

    fun getFirstLoginStatus(): Boolean {
        return prefs.getBoolean("first_login", false)
    }
}
