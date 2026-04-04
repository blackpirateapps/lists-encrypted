package com.blackpiratex.flowye2ee.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blackpiratex.flowye2ee.FlowyApp
import com.blackpiratex.flowye2ee.data.crypto.CryptoManager
import com.blackpiratex.flowye2ee.data.crypto.JsonSerializer
import com.blackpiratex.flowye2ee.data.crypto.KeyManager
import com.blackpiratex.flowye2ee.domain.usecase.EncryptionUseCases
import com.blackpiratex.flowye2ee.presentation.ui.editor.EditorScreen
import com.blackpiratex.flowye2ee.presentation.ui.onboarding.OnboardingScreen
import com.blackpiratex.flowye2ee.presentation.ui.settings.SettingsScreen
import com.blackpiratex.flowye2ee.presentation.viewmodel.EditorViewModel
import com.blackpiratex.flowye2ee.presentation.viewmodel.SettingsViewModel
import com.blackpiratex.flowye2ee.presentation.viewmodel.OnboardingViewModel
import com.blackpiratex.flowye2ee.presentation.viewmodel.PasswordPromptViewModel
import com.blackpiratex.flowye2ee.presentation.ui.onboarding.PasswordPromptScreen

@Composable
fun RootNav() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as FlowyApp
    val repository = app.repository
    val keyManager = remember { KeyManager(context, CryptoManager("flowy_master_key"), JsonSerializer()) }
    val encryptionUseCases = remember { EncryptionUseCases(keyManager, repository) }
    val editorViewModel = remember { EditorViewModel(repository) }
    val settingsViewModel = remember { SettingsViewModel(encryptionUseCases) }
    val onboardingViewModel = remember { OnboardingViewModel(keyManager) }
    val passwordPromptViewModel = remember { PasswordPromptViewModel(keyManager) }

    val startDestination = remember { mutableStateOf("loading") }
    LaunchedEffect(Unit) {
        val done = runCatching { keyManager.isOnboardingComplete() }.getOrDefault(false)
        startDestination.value = if (!done) {
            "onboarding"
        } else if (runCatching { keyManager.hasPasswordConfigured() }.getOrDefault(false)) {
            "password"
        } else {
            "editor"
        }
    }
    if (startDestination.value == "loading") return
    NavHost(navController = navController, startDestination = startDestination.value) {
        composable("onboarding") {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onDone = { navController.navigate("editor") { popUpTo("onboarding") { inclusive = true } } }
            )
        }
        composable("password") {
            PasswordPromptScreen(
                viewModel = passwordPromptViewModel,
                onUnlocked = { navController.navigate("editor") { popUpTo("password") { inclusive = true } } }
            )
        }
        composable("editor") {
            EditorScreen(
                viewModel = editorViewModel,
                onOpenSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
