package com.example.app.data.repository

import com.example.app.data.local.AccessLogDao
import com.example.app.domain.AccessLog
import com.example.app.domain.AccessLogRepository
import javax.inject.Inject

class AccessLogRepositoryImpl @Inject constructor(
    private val accessLogDao: AccessLogDao
) : AccessLogRepository {
    override suspend fun logAccess(userId: String, type: String) {
        val log = AccessLog(
            userId = userId,
            dateTime = System.currentTimeMillis(),
            type = type
        )
        accessLogDao.insertLog(log)
    }

    override suspend fun getLogsForUser(userId: String): List<AccessLog> {
        return accessLogDao.getLogsForUser(userId)
    }
}
