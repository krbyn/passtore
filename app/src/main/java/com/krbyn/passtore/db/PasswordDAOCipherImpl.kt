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
import com.krbyn.passtore.db.PasswordDAO.Companion.DB_NAME
import com.krbyn.passtore.pass.PassField
import com.krbyn.passtore.pass.SimplePassword
import com.krbyn.passtore.sec.LocalPasswordStore

/**
 * Implementation of [PasswordDAO] interface
 * By extending [net.sqlcipher.database.SQLiteOpenHelper] data et rest is encrypted
 * To decrypt and access the data [LocalPasswordStore] is used, which is the only class
 * aware of the password when user is logged in
 */
internal class PasswordDAOCipherImpl(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, VERSION), PasswordDAO {
    companion object {
        private const val VERSION = 1
        private const val TABLE_NAME = "PASS_TABLE"
        private const val COL_ID = "ID"
        private const val COL_LAST_UPDATE = "LAST_UPDATE"
        private const val COL_PASSWORD_TYPE = "PASSWORD_TYPE"
        private const val COL_NAME = "NAME"
        private const val COL_VALUE = "VALUE"
        private const val CREATE_PASSWORD_TABLE =
                "CREATE TABLE $TABLE_NAME(" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COL_LAST_UPDATE BIGINT DEFAULT(strftime('%Y-%m-%d %H:%M:%f', 'now')), " +
                        "$COL_PASSWORD_TYPE  TEXT,  " +
                        "$COL_NAME  TEXT,  " +
                        "$COL_VALUE TEXT )"

        private val gson = Gson()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_PASSWORD_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, v1: Int, v2: Int) {
        if (v2 <= v1) {
            return
        }
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL(CREATE_PASSWORD_TABLE)
    }

    override fun insertPassword(pass: Password): Password {
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getDatabase(this)
            val values = ContentValues()
            values.put(COL_NAME, pass.name)
            values.put(COL_PASSWORD_TYPE, pass.type)
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

    override fun updatePassword(pass: Password): Boolean {
        if (pass.id <= 0) {
            return false
        }
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getDatabase(this)
            val values = ContentValues()
            values.put(COL_NAME, pass.name)
            values.put(COL_PASSWORD_TYPE, pass.type)
            values.put(COL_VALUE, gson.toJson(pass.fields))
            val rows = db?.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(pass.id.toString()))
            return rows != null && rows > 0
        } finally {
            db?.close()
        }
    }

    override fun deletePassword(id: Long): Boolean {
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getDatabase(this)
            val rows = db?.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))
            return rows != null && rows > 0
        } finally {
            db?.close()
        }
    }

    override fun getPassword(id: Long): Password? {
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getDatabase(this)
            return db?.query(
                    TABLE_NAME,
                    arrayOf(COL_NAME, COL_PASSWORD_TYPE, COL_VALUE),
                    "$COL_ID=?",
                    arrayOf(id.toString()),
                    null, null, null)?.use {
                if (it.moveToFirst()) {
                    val listType = object : TypeToken<ArrayList<PassField>>() {}.type
                    Password(
                            id,
                            it.getString(it.getColumnIndex(COL_NAME)),
                            it.getString(it.getColumnIndex(COL_PASSWORD_TYPE)),
                            gson.fromJson(it.getString(it.getColumnIndex(COL_VALUE)), listType))
                } else {
                    null
                }
            }

        } finally {
            db?.close()
        }
    }

    override fun getAllPasswordNames(sort: PasswordDAO.SortBy): List<SimplePassword> {
        var db: SQLiteDatabase? = null
        try {
            db = LocalPasswordStore.getDatabase(this)
            val orderBy = when(sort) {
                PasswordDAO.SortBy.ID -> COL_ID
                PasswordDAO.SortBy.NAME -> COL_NAME
                PasswordDAO.SortBy.TYPE -> COL_PASSWORD_TYPE
                PasswordDAO.SortBy.LAST_UPDATE -> COL_LAST_UPDATE
            }
            return db?.query(
                    TABLE_NAME,
                    arrayOf(COL_ID, COL_NAME, COL_PASSWORD_TYPE),
                    null,
                    null, null, null, orderBy)?.use {
                val passwords = ArrayList<SimplePassword>(it.count)
                if (it.moveToFirst()) {
                    do {
                        passwords.add(SimplePassword(
                                it.getLong(it.getColumnIndex(COL_ID)),
                                it.getString(it.getColumnIndex(COL_NAME)),
                                it.getString(it.getColumnIndex(COL_PASSWORD_TYPE))
                        ))
                    } while(it.moveToNext())
                }
                passwords
            }?: emptyList()
        } finally {
            db?.close()
        }
    }
}