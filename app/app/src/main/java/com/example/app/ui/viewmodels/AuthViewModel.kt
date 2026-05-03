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
import com.example.app.domain.UserRepository
import com.example.app.domain.AccessLogRepository
import com.example.app.domain.User
import com.example.app.ui.screens.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val accessLogRepository: AccessLogRepository
) : ViewModel() {
    private val TAG = "AuthLog"

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")
    var birthdate by mutableStateOf("")
    
    var address by mutableStateOf("")
    var country by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var acceptEmails by mutableStateOf(true)
    var acceptTerms by mutableStateOf(false)
    
    var isLoginMode by mutableStateOf(true)
    var errorMessage by mutableStateOf<UiText?>(null)
    var isLoading by mutableStateOf(false)

    fun isUserLoggedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }

    fun onAuthAction(
        navController: NavHostController,
        onUserDataSaved: (String, String) -> Unit
    ) {
        if (isLoginMode) {
            handleLogin(navController)
        } else {
            viewModelScope.launch {
                if (email.isBlank() || password.isBlank() || username.isBlank() || birthdate.isBlank()) {
                    errorMessage = UiText.StringResource(R.string.auth_error_all_fields)
                    return@launch
                }
                if (!acceptTerms) {
                    errorMessage = UiText.DynamicString("Debes aceptar los términos y condiciones")
                    return@launch
                }
                if (!userRepository.isUsernameAvailable(username)) {
                    errorMessage = UiText.DynamicString("El nombre de usuario ya está en uso")
                    return@launch
                }
                if (password.length < 6) {
                    errorMessage = UiText.StringResource(R.string.auth_error_password_short)
                    return@launch
                }
                handleSignup(navController, onUserDataSaved)
            }
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
                    val firebaseUser = authRepository.getCurrentUser()
                    if (firebaseUser != null && authRepository.isEmailVerified()) {
                        accessLogRepository.logAccess(firebaseUser.uid, "LOGIN")
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    } else {
                        errorMessage = UiText.StringResource(R.string.auth_error_verify_email)
                        authRepository.sendEmailVerification()
                        authRepository.signOut()
                    }
                },
                onFailure = { exception ->
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
                val firebaseUser = authRepository.getCurrentUser()
                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        email = email,
                        username = username,
                        birthdate = birthdate,
                        address = address,
                        country = country,
                        phoneNumber = phoneNumber,
                        acceptEmails = acceptEmails
                    )
                    userRepository.insertUser(newUser)
                    authRepository.sendEmailVerification()
                    onUserDataSaved(username, birthdate)
                    isLoading = false
                    errorMessage = UiText.StringResource(R.string.auth_signup_success)
                    isLoginMode = true
                    password = "" 
                }
            } else {
                isLoading = false
                val msg = result.exceptionOrNull()?.localizedMessage
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

    fun signOut() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid
            if (uid != null) {
                accessLogRepository.logAccess(uid, "LOGOUT")
            }
            authRepository.signOut()
        }
    }
}
