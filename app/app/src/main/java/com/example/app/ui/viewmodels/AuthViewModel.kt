package com.example.app.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.screens.UiText
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
            // Registro directo con todos los campos
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

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Log.d(TAG, "Login success")
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    } else {
                        Log.w(TAG, "Email not verified")
                        errorMessage = UiText.StringResource(R.string.auth_error_verify_email)
                        user?.sendEmailVerification()
                        auth.signOut()
                    }
                } else {
                    Log.e(TAG, "Login failed", task.exception)
                    errorMessage = task.exception?.localizedMessage?.let {
                        UiText.StringResource(R.string.auth_error_prefix, it)
                    } ?: UiText.StringResource(R.string.auth_error_unknown)
                }
            }
    }

    private fun handleSignup(
        navController: NavHostController,
        onUserDataSaved: (String, String) -> Unit
    ) {
        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Signup success")
                    auth.currentUser?.sendEmailVerification()
                    
                    // Guardar datos localmente
                    onUserDataSaved(username, birthdate)
                    
                    isLoading = false
                    errorMessage = UiText.StringResource(R.string.auth_signup_success)
                    
                    // Resetear para volver al login tras verificar
                    isLoginMode = true
                    password = "" 
                } else {
                    isLoading = false
                    Log.e(TAG, "Signup failed", task.exception)
                    errorMessage = task.exception?.localizedMessage?.let {
                        UiText.StringResource(R.string.auth_error_prefix, it)
                    } ?: UiText.StringResource(R.string.auth_error_unknown)
                }
            }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (UiText) -> Unit) {
        if (email.isBlank()) {
            onError(UiText.StringResource(R.string.auth_error_reset_email))
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val errorMsg = task.exception?.localizedMessage
                    onError(
                        if (errorMsg != null) UiText.StringResource(R.string.auth_error_prefix, errorMsg)
                        else UiText.StringResource(R.string.auth_error_unknown)
                    )
                }
            }
    }

    fun signout() {
        auth.signOut()
    }
}
