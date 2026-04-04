package com.blackpiratex.flowye2ee.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackpiratex.flowye2ee.data.crypto.KeyManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val keyManager: KeyManager
) : ViewModel() {
    private val _done = MutableStateFlow(false)
    val done: StateFlow<Boolean> = _done

    fun enablePassword(password: CharArray) {
        viewModelScope.launch {
            keyManager.enablePassword(password)
            keyManager.setOnboardingComplete()
            _done.value = true
        }
    }

    fun skip() {
        viewModelScope.launch {
            keyManager.ensureDeviceKey()
            keyManager.setOnboardingComplete()
            _done.value = true
        }
    }
}
