package com.krbyn.passtore.db

import android.content.Context
import com.krbyn.passtore.pass.Password
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.krbyn.passtore.common.Const
import com.krbyn.passtore.pass.PassField
import com.krbyn.passtore.sec.LocalPasswordStore
import net.sqlcipher.Cursor

class PasswordDAO(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, VERSION) {
    companion object {
        private const val VERSION = 1
        private const val DB_NAME = "PasstoreDBName"
        private const val TABLE_NAME = "PASS_TABLE"
        private const val COL_ID = "ID"
        private const val COL_LAST_UPDATE = "LAST_UPDATE"
        private const val COL_NAME = "NAME"
        private const val COL_VALUE = "VALUE"
        private const val CREATE_PASSWORD_TABLE =
                "CREATE TABLE $TABLE_NAME (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_LAST_UPDATE BIGINT DEFAULT strftime(\"%Y-%m-%d %H:%M:%f\", \"now\") " +
                        "$COL_NAME  TEXT,  " +
                        "$COL_VALUE TEXT )"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_PASSWORD_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, v1: Int, v2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL(CREATE_PASSWORD_TABLE)
    }

    fun insertPassword(pass: Password): Password {
        val gson = Gson()
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getWritableDatabase(this)
            val values = ContentValues()
            values.put(COL_NAME, pass.name)
            values.put(COL_VALUE, gson.toJson(pass.fields))
            val id = db?.insert(TABLE_NAME, null, values)
            if (id == null || id < 1) {
                Log.e(Const.LOG_TAG, "Failed to save password")
            } else {
                pass.id = id
            }
            return pass
        } finally {
            db?.close()
        }
    }

    fun updatePassword(pass: Password): Boolean {
        val gson = Gson()
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getWritableDatabase(this)
            val values = ContentValues()
            values.put(COL_NAME, pass.name)
            values.put(COL_VALUE, gson.toJson(pass.fields))
            val rows = db?.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(pass.id.toString()))
            return rows != null && rows > 0
        } finally {
            db?.close()
        }
    }

    fun deletePassword(id: Long): Boolean {
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getWritableDatabase(this)
            val rows = db?.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))
            return rows != null && rows > 0
        } finally {
            db?.close()
        }
    }

    fun getPassword(id: Long): Password? {
        val gson = Gson()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = LocalPasswordStore.getReadableDatabase(this)
            cursor = db?.query(
                    TABLE_NAME,
                    arrayOf(COL_NAME, COL_VALUE),
                    "$COL_ID=?",
                    arrayOf(id.toString()),
                    null, null, null) ?: return null
            if (cursor.moveToFirst()) {
                val listType = object : TypeToken<ArrayList<PassField>>() {}.type
                return Password(
                        id,
                        cursor.getString(cursor.getColumnIndex(COL_NAME)),
                        gson.fromJson(cursor.getString(cursor.getColumnIndex(COL_VALUE)), listType))
            }
        } finally {
            cursor?.close()
            db?.close()
        }
        return null
    }

    fun getAllPasswords(): List<Password> {
        val gson = Gson()
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = LocalPasswordStore.getReadableDatabase(this)
            cursor = db?.query(
                    TABLE_NAME,
                    arrayOf(COL_NAME, COL_VALUE),
                    null,
                    null, null, null, null) ?: return emptyList()
            val passwords = ArrayList<Password>()
            if (cursor.moveToFirst()) {
                do {
                    val listType = object : TypeToken<ArrayList<PassField>>() {}.type
                    passwords.add(
                            Password(
                                    cursor.getLong(cursor.getColumnIndex(COL_ID)),
                                    cursor.getString(cursor.getColumnIndex(COL_NAME)),
                                    gson.fromJson(cursor.getString(cursor.getColumnIndex(COL_VALUE)), listType))
                    )
                } while(cursor.moveToNext())
            }
            return passwords
        } finally {
            cursor?.close()
            db?.close()
        }
    }
}