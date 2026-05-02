package com.example.app.domain

interface AccessLogRepository {
    suspend fun logAccess(userId: String, type: String)
    suspend fun getLogsForUser(userId: String): List<AccessLog>
}
