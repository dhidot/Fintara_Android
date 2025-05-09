package com.bcafinance.fintara.config.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Menyimpan token
    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    // Mengambil token
    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    // Menyimpan nama pengguna
    fun saveUserName(name: String) {
        prefs.edit().putString("name", name).apply()
    }

    // Mengambil nama pengguna
    fun getUserName(): String? {
        return prefs.getString("name", null)
    }

    // Menyimpan status login pertama kali
    fun saveFirstLoginStatus(isFirstLogin: Boolean) {
        prefs.edit().putBoolean("first_login", isFirstLogin).apply()
    }

    // Mengambil status login pertama kali
    fun getFirstLoginStatus(): Boolean {
        return prefs.getBoolean("first_login", false)
    }

    // Menghapus data session
    fun clearSession() {
        val editor = prefs.edit()
        editor.remove("token")
        editor.remove("name")
        editor.remove("first_login")
        editor.apply()
    }

    // Mengecek apakah sudah login
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
