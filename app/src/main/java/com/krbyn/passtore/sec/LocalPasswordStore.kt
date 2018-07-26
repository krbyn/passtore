package com.krbyn.passtore.sec

import android.util.Base64
import com.krbyn.passtore.app.PasstoreApp
import com.krbyn.passtore.common.Const
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import java.nio.charset.Charset

object LocalPasswordStore {
    private val UTF = Charset.forName("UTF-8")
    @Volatile
    private var localPassword: String? = null

    fun login(password: String): Boolean {
        val storedPassEncoded = PasstoreApp.getSharedPreferences().getString(Const.SHARED_PREF_PASS_KEY, null) ?: return false
        val storedPass = decodeString(storedPassEncoded)
        return if (HashHelper.validatePassword(password, storedPass)) {
            localPassword = encodeString(password)
            true
        } else {
            false
        }
    }

    fun isRegistered(): Boolean {
        val storedPass = PasstoreApp.getSharedPreferences().getString(Const.SHARED_PREF_PASS_KEY, null)
        return storedPass != null
    }

    fun unregister() {
        val storedPass = PasstoreApp.getSharedPreferences().getString(Const.SHARED_PREF_PASS_KEY, null)
        if (storedPass != null) {
            with(PasstoreApp.getSharedPreferences().edit()) {
                remove(Const.SHARED_PREF_PASS_KEY)
                apply()
            }
            localPassword = null
        }
    }

    fun isLoggedIn(): Boolean {
        return localPassword != null
    }

    fun logout() {
        localPassword = null
    }

    fun register(password: String) {
        val hashedPass = HashHelper.generatePasswordHash(password)
        val hashedPassEncoded = encodeString(hashedPass)
        with(PasstoreApp.getSharedPreferences().edit()) {
            putString(Const.SHARED_PREF_PASS_KEY, hashedPassEncoded)
            apply()
        }
        localPassword = encodeString(password)
    }

    fun getDatabase(sql: SQLiteOpenHelper): SQLiteDatabase? {
        return sql.getWritableDatabase(localPassword?.let { decodeString(it) })
    }

    private fun encodeString(s: String): String {
        val data = s.toByteArray(UTF)
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    private fun decodeString(encoded: String): String {
        val dataDec = Base64.decode(encoded, Base64.DEFAULT)
        return String(dataDec, UTF)
    }
}