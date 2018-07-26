package com.krbyn.passtore.db

import android.content.Context

object PasswordDataService {
    fun getPasswordDAO(context: Context): PasswordDAO {
        return PasswordDAOCipherImpl(context)
    }
}