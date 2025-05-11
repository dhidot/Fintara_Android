package com.bcafinance.fintara.config.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Menyimpan nama pengguna
    fun saveUserName(name: String) {
        prefs.edit().putString("name", name).apply()
    }

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun saveUserProfile(name: String, email: String, photoUrl: String?) {
        prefs.edit().apply {
            putString("name", name)
            putString("email", email)
            putString("photoUrl", photoUrl)
        }.apply()
    }

    fun saveFirstLogin(isFirstLogin: Boolean) {
        prefs.edit().putBoolean("firstLogin", isFirstLogin).apply()
    }

    fun getToken(): String? = prefs.getString("token", null)
    fun isFirstLogin(): Boolean = prefs.getBoolean("firstLogin", false)
    fun getName(): String? = prefs.getString("name", null)
    fun getEmail(): String? = prefs.getString("email", null)
    fun getPhotoUrl(): String? = prefs.getString("photoUrl", null)

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
