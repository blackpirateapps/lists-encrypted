package com.blackpiratex.flowye2ee.data.crypto

import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object KeyDerivation {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256

    fun randomSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val bytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(bytes, "AES")
    }

    fun randomKey(): SecretKey {
        val bytes = ByteArray(KEY_LENGTH / 8)
        SecureRandom().nextBytes(bytes)
        return SecretKeySpec(bytes, "AES")
    }
}
