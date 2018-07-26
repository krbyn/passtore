package com.krbyn.passtore.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.krbyn.passtore.common.Const
import net.sqlcipher.database.SQLiteDatabase
import java.io.File

class PasstoreApp: Application() {
    companion object {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var databaseFile: File

        fun getSharedPreferences(): SharedPreferences {
            return sharedPref
        }

        fun getDatabaseFile(): File {
            return databaseFile
        }

        fun dropOrCreateDBFile(file: File) {
            databaseFile = file
            databaseFile.mkdirs()
            databaseFile.delete()
        }
    }

    override fun onCreate() {
        super.onCreate()
        //Load native libs for SQLCipher to be used in SQLiteOpenHelper
        SQLiteDatabase.loadLibs(this)
        //Set shared preferences to LocalPasswordManager
        sharedPref = getSharedPreferences(Const.SHARED_PREFERENCE_ID, Context.MODE_PRIVATE)
        val firstStart = sharedPref.getBoolean(Const.SHARED_PREF_FIRST_START, true)
        databaseFile = getDatabasePath("passtore.db")
        if (firstStart) {
            dropOrCreateDBFile(databaseFile)
            with(sharedPref.edit()) {
                putBoolean(Const.SHARED_PREF_FIRST_START, false)
                apply()
            }
        }
    }
}