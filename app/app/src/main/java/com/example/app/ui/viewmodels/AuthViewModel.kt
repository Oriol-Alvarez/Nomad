package com.example.app.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.Routes
import com.example.app.domain.AuthRepository
import com.example.app.data.repository.AuthRepositoryImpl
import com.example.app.ui.screens.UiText
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {
    private val TAG = "AuthLog"

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")
    var birthdate by mutableStateOf("")
    
    var isLoginMode by mutableStateOf(true)
    
    var errorMessage by mutableStateOf<UiText?>(null)
    var isLoading by mutableStateOf(false)

    fun onAuthAction(
        navController: NavHostController,
        onUserDataSaved: (String, String) -> Unit
    ) {
        if (isLoginMode) {
            handleLogin(navController)
        } else {
            if (email.isBlank() || password.isBlank() || username.isBlank() || birthdate.isBlank()) {
                errorMessage = UiText.StringResource(R.string.auth_error_all_fields)
                return
            }
            if (password.length < 6) {
                errorMessage = UiText.StringResource(R.string.auth_error_password_short)
                return
            }
            handleSignup(navController, onUserDataSaved)
        }
    }

    private fun handleLogin(navController: NavHostController) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = UiText.StringResource(R.string.auth_error_campos)
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            isLoading = false
            
            result.fold(
                onSuccess = {
                    val user = authRepository.getCurrentUser()
                    if (user != null && authRepository.isEmailVerified()) {
                        Log.d(TAG, "Login success")
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    } else {
                        Log.w(TAG, "Email not verified")
                        errorMessage = UiText.StringResource(R.string.auth_error_verify_email)
                        authRepository.sendEmailVerification()
                        authRepository.signOut()
                    }
                },
                onFailure = { exception ->
                    Log.e(TAG, "Login failed", exception)
                    val msg = exception.localizedMessage
                    errorMessage = if (msg != null) UiText.DynamicString(msg)
                                   else UiText.StringResource(R.string.auth_error_unknown)
                }
            )
        }
    }

    private fun handleSignup(
        navController: NavHostController,
        onUserDataSaved: (String, String) -> Unit
    ) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val result = authRepository.signup(email, password)
            if (result.isSuccess) {
                Log.d(TAG, "Signup success")
                authRepository.sendEmailVerification()
                
                // Guardar datos localmente
                onUserDataSaved(username, birthdate)
                
                isLoading = false
                errorMessage = UiText.StringResource(R.string.auth_signup_success)
                
                // Resetear para volver al login tras verificar
                isLoginMode = true
                password = "" 
            } else {
                isLoading = false
                val exception = result.exceptionOrNull()
                Log.e(TAG, "Signup failed", exception)
                val msg = exception?.localizedMessage
                errorMessage = if (msg != null) UiText.DynamicString(msg)
                               else UiText.StringResource(R.string.auth_error_unknown)
            }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (UiText) -> Unit) {
        if (email.isBlank()) {
            onError(UiText.StringResource(R.string.auth_error_reset_email))
            return
        }
        
        viewModelScope.launch {
            val result = authRepository.resetPassword(email)
            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { exception ->
                    val msg = exception.localizedMessage
                    onError(if (msg != null) UiText.DynamicString(msg)
                            else UiText.StringResource(R.string.auth_error_unknown))
                }
            )
        }
    }

    fun signout() {
        authRepository.signOut()
    }
}
