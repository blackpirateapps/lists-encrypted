package com.blackpiratex.flowye2ee.presentation.state

data class SettingsState(
    val encryptionEnabled: Boolean = false,
    val hasPasswordConfigured: Boolean = false
)
