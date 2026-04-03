package com.blackpiratex.flowye2ee.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager(
    private val keyAlias: String
) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    fun getOrCreateSecretKey(): SecretKey {
        val existingKey = keyStore.getKey(keyAlias, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun encrypt(plainBytes: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = getOrCreateSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val cipherText = cipher.doFinal(plainBytes)
        val buffer = ByteBuffer.allocate(4 + iv.size + cipherText.size)
        buffer.putInt(iv.size)
        buffer.put(iv)
        buffer.put(cipherText)
        return buffer.array()
    }

    fun decrypt(cipherBytes: ByteArray): ByteArray {
        val buffer = ByteBuffer.wrap(cipherBytes)
        val ivLength = buffer.int
        val iv = ByteArray(ivLength)
        buffer.get(iv)
        val cipherText = ByteArray(buffer.remaining())
        buffer.get(cipherText)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = getOrCreateSecretKey()
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        return cipher.doFinal(cipherText)
    }
}
