package com.blackpiratex.flowye2ee.domain.usecase

import com.blackpiratex.flowye2ee.data.crypto.KeyManager
import com.blackpiratex.flowye2ee.data.repository.NodeRepository

class EncryptionUseCases(
    private val keyManager: KeyManager,
    private val nodeRepository: NodeRepository
) {
    suspend fun isEnabled(): Boolean = keyManager.isEncryptionEnabled()

    suspend fun hasPasswordConfigured(): Boolean = keyManager.hasPasswordConfigured()

    suspend fun enablePassword(password: CharArray) {
        keyManager.enablePassword(password)
    }

    suspend fun changePassword(newPassword: CharArray) {
        nodeRepository.reEncryptAll("".toCharArray(), newPassword)
    }

    suspend fun unlock(password: CharArray) {
        keyManager.setPassword(password)
    }
}
