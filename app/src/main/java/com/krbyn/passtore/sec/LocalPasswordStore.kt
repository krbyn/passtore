package com.krbyn.passtore.sec

import android.util.Base64
import com.krbyn.passtore.app.PasstoreApp
import com.krbyn.passtore.common.Const
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import org.jetbrains.annotations.NotNull
import java.nio.charset.Charset

object LocalPasswordStore {
    private val UTF = Charset.forName("UTF-8")
    @Volatile
    private var localPassword: String? = null

    fun login(password: String): Boolean {
        val storedPassEncoded = PasstoreApp.getSharedPreferences().getString(Const.SHARED_PREF_PASS_KEY, null) ?: return false
        val storedPass = decodeString(storedPassEncoded)
        if (HashHelper.validatePassword(password, storedPass)) {
            localPassword = encodeString(password)
            return true
        } else {
            return false
        }
    }

    fun isRegistered(): Boolean {
        val storedPass = PasstoreApp.getSharedPreferences().getString(Const.SHARED_PREF_PASS_KEY, null)
        return storedPass != null
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
            this?.putString(Const.SHARED_PREF_PASS_KEY, hashedPassEncoded)
            this?.apply()
        }
        localPassword = encodeString(password)
    }

    fun getReadableDatabase(sqLite: SQLiteOpenHelper): SQLiteDatabase? {
        return sqLite.getReadableDatabase(localPassword?.let { decodeString(it) })
    }

    fun getWritableDatabase(sqLite: SQLiteOpenHelper): SQLiteDatabase? {
        return sqLite.getWritableDatabase(localPassword?.let { decodeString(it) })
    }

    @NotNull
    private fun encodeString(s: String): String {
        val data = s.toByteArray(UTF)
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    @NotNull
    private fun decodeString(@NotNull encoded: String): String {
        val dataDec = Base64.decode(encoded, Base64.DEFAULT)
        return String(dataDec, UTF)
    }
}