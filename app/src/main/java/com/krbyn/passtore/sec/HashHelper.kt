package com.krbyn.passtore.sec

import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.experimental.xor

internal object HashHelper {
    fun validatePassword(originalPassword: String, storedPassword: String): Boolean {
        val parts = storedPassword.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val iterations = Integer.parseInt(parts[0])
        val salt = fromHex(parts[1])
        val hash = fromHex(parts[2])

        val testHash = getHash(originalPassword, salt, iterations, hash.size * 8)

        var diff = hash.size xor testHash.size
        var i = 0
        while (i < hash.size && i < testHash.size) {
            diff = diff or ((hash[i] xor testHash[i]).toInt())
            i++
        }
        return diff == 0
    }

    private fun getHash(password: String, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val testHash = skf.generateSecret(spec).encoded
        return testHash
    }

    private fun fromHex(hex: String): ByteArray {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            bytes[i] = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16).toByte()
        }
        return bytes
    }

    fun generatePasswordHash(password: String): String {
        val iterations = 1000
        val chars = password.toCharArray()
        val salt = getSalt()

        val hash = getHash(password, salt, iterations, 64 * 8)

        return "$iterations:${toHex(salt)}:${toHex(hash)}"
    }

    private fun getSalt(): ByteArray {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val salt = ByteArray(16)
        sr.nextBytes(salt)
        return salt
    }

    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) {
            String.format("%0" + paddingLength + "d", 0) + hex
        } else {
            hex
        }
    }
}