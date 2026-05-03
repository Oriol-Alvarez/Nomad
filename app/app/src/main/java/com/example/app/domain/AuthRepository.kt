package com.example.app.domain

import com.example.app.ui.screens.UiText
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): FirebaseUser?
    fun getAuthStateFlow(): Flow<FirebaseUser?>
    fun isEmailVerified(): Boolean
    fun signOut()
    
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signup(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
}
