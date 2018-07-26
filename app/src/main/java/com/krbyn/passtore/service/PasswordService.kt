package com.krbyn.passtore.service

import com.krbyn.passtore.async.Feature
import com.krbyn.passtore.pass.Password

interface PasswordService {
    fun createPassword(password: Password): Feature<Boolean>
    fun getAllPasswords(): Feature<List<Boolean>>
}