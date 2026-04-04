package com.blackpiratex.flowye2ee.data.crypto

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.crypto.SecretKey

private val Context.cryptoDataStore by preferencesDataStore("crypto")

class KeyManager(
    private val context: Context,
    private val cryptoManager: CryptoManager,
    private val serializer: JsonSerializer
) {
    private val saltKey = stringPreferencesKey("salt")
    private val wrappedKey = stringPreferencesKey("wrapped_key")
    private val enabledKey = stringPreferencesKey("encryption_enabled")
    private val createdAtKey = longPreferencesKey("key_created_at")
    private val onboardingKey = stringPreferencesKey("onboarding_complete")

    private var cachedKey: SecretKey? = null

    val encryptionEnabledFlow: Flow<Boolean> = context.cryptoDataStore.data.map { prefs ->
        prefs[enabledKey]?.toBooleanStrictOrNull() ?: false
    }

    suspend fun isEncryptionEnabled(): Boolean {
        return context.cryptoDataStore.data.first()[enabledKey]?.toBooleanStrictOrNull() ?: false
    }

    suspend fun ensureDeviceKey(): SecretKey {
        cachedKey?.let { return it }
        val master = cryptoManager.getOrCreateSecretKey()
        val existing = context.cryptoDataStore.data.first()[wrappedKey]
        if (!existing.isNullOrBlank()) {
            val payload = serializer.decodePayload(existing)
            val raw = cryptoManager.decrypt(payload, master)
            return SecretKeySpecCompat.from(raw).also { cachedKey = it }
        }
        val deviceKey = KeyDerivation.randomKey()
        val payload = cryptoManager.encrypt(deviceKey.encoded, master)
        context.cryptoDataStore.edit { prefs ->
            prefs[wrappedKey] = serializer.encodePayload(payload)
            prefs[enabledKey] = false.toString()
            prefs[createdAtKey] = System.currentTimeMillis()
        }
        cachedKey = deviceKey
        return deviceKey
    }

    suspend fun enablePassword(password: CharArray) {
        val salt = KeyDerivation.randomSalt()
        val derived = KeyDerivation.deriveKey(password, salt)
        context.cryptoDataStore.edit { prefs ->
            prefs[saltKey] = Base64.encodeToString(salt, Base64.NO_WRAP)
            prefs[wrappedKey] = ""
            prefs[enabledKey] = true.toString()
            prefs[createdAtKey] = System.currentTimeMillis()
        }
        cachedKey = derived
    }

    suspend fun getActiveKey(): SecretKey {
        cachedKey?.let { return it }
        val enabled = isEncryptionEnabled()
        return if (enabled) {
            throw IllegalStateException("Password required")
        } else {
            ensureDeviceKey()
        }
    }

    suspend fun setPassword(password: CharArray): SecretKey {
        val prefs = context.cryptoDataStore.data.first()
        val saltEncoded = prefs[saltKey] ?: throw IllegalStateException("Missing salt")
        val salt = Base64.decode(saltEncoded, Base64.NO_WRAP)
        val derived = KeyDerivation.deriveKey(password, salt)
        cachedKey = derived
        return derived
    }

    fun requiresPassword(): Boolean {
        return cachedKey == null
    }

    suspend fun changePassword(newPassword: CharArray) {
        val newSalt = KeyDerivation.randomSalt()
        val newKey = KeyDerivation.deriveKey(newPassword, newSalt)
        context.cryptoDataStore.edit { prefs ->
            prefs[saltKey] = Base64.encodeToString(newSalt, Base64.NO_WRAP)
            prefs[wrappedKey] = ""
            prefs[enabledKey] = true.toString()
            prefs[createdAtKey] = System.currentTimeMillis()
        }
        cachedKey = newKey
    }

    suspend fun hasPasswordConfigured(): Boolean {
        val prefs = context.cryptoDataStore.data.first()
        return prefs[saltKey] != null && (prefs[enabledKey]?.toBooleanStrictOrNull() ?: false)
    }

    suspend fun isOnboardingComplete(): Boolean {
        return context.cryptoDataStore.data.first()[onboardingKey]?.toBooleanStrictOrNull() ?: false
    }

    suspend fun setOnboardingComplete() {
        context.cryptoDataStore.edit { prefs ->
            prefs[onboardingKey] = true.toString()
        }
    }

    suspend fun clearCachedKey() {
        cachedKey = null
    }
}

internal object SecretKeySpecCompat {
    fun from(raw: ByteArray): SecretKey {
        return javax.crypto.spec.SecretKeySpec(raw, "AES")
    }
}
