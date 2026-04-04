package com.blackpiratex.flowye2ee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackpiratex.flowye2ee.domain.usecase.EncryptionUseCases
import com.blackpiratex.flowye2ee.presentation.state.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val encryptionUseCases: EncryptionUseCases
): ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                encryptionEnabled = encryptionUseCases.isEnabled(),
                hasPasswordConfigured = encryptionUseCases.hasPasswordConfigured()
            )
        }
    }

    fun enablePassword(password: CharArray) {
        viewModelScope.launch {
            encryptionUseCases.enablePassword(password)
            load()
        }
    }

    fun changePassword(newPassword: CharArray) {
        viewModelScope.launch {
            encryptionUseCases.changePassword(newPassword)
            load()
        }
    }
}
