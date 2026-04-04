package com.blackpiratex.flowye2ee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackpiratex.flowye2ee.data.crypto.KeyManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasswordPromptViewModel(
    private val keyManager: KeyManager
) : ViewModel() {
    private val _unlocked = MutableStateFlow(false)
    val unlocked: StateFlow<Boolean> = _unlocked

    fun unlock(password: CharArray) {
        viewModelScope.launch {
            runCatching {
                keyManager.setPassword(password)
                _unlocked.value = true
            }
        }
    }
}
