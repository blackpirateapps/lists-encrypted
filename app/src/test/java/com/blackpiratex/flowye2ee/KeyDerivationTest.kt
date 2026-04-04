package com.blackpiratex.flowye2ee

import com.blackpiratex.flowye2ee.data.crypto.KeyDerivation
import org.junit.Assert.assertNotEquals
import org.junit.Test

class KeyDerivationTest {
    @Test
    fun randomKeysDiffer() {
        val keyA = KeyDerivation.randomKey()
        val keyB = KeyDerivation.randomKey()
        assertNotEquals(keyA.encoded.toList(), keyB.encoded.toList())
    }
}
