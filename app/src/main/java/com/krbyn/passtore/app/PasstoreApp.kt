package com.krbyn.passtore.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.krbyn.passtore.common.Const
import net.sqlcipher.database.SQLiteDatabase
import org.jetbrains.annotations.NotNull

class PasstoreApp: Application() {
    companion object {
        private lateinit var sharedPref: SharedPreferences
        @NotNull
        fun getSharedPreferences(): SharedPreferences {
            return sharedPref
        }
    }

    override fun onCreate() {
        //Load native libs for SQLCipher to be used in SQLiteOpenHelper
        SQLiteDatabase.loadLibs(applicationContext)
        //Set shared preferences to LocalPasswordManager
        sharedPref = getSharedPreferences(Const.SHARED_PREFERENCE_ID, Context.MODE_PRIVATE)
        super.onCreate()
    }
}