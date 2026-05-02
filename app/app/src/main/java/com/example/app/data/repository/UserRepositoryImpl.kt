package com.example.app.data.repository

import com.example.app.data.local.UserDao
import com.example.app.domain.User
import com.example.app.domain.UserRepository

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getUserById(id: String): User? = userDao.getUserById(id)
    
    override suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    
    override suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    
    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    override suspend fun isUsernameAvailable(username: String): Boolean {
        return userDao.getUserByUsername(username) == null
    }
}
