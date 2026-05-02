package com.example.app.domain

interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun isUsernameAvailable(username: String): Boolean
}
