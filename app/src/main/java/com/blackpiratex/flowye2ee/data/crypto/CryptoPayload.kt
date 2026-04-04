package com.blackpiratex.flowye2ee.data.crypto

import kotlinx.serialization.Serializable

@Serializable
data class CryptoPayload(
    val iv: ByteArray,
    val cipherText: ByteArray
)
