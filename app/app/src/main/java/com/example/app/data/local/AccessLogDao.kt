package com.example.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app.domain.AccessLog

@Dao
interface AccessLogDao {
    @Insert
    suspend fun insertLog(log: AccessLog)

    @Query("SELECT * FROM access_logs WHERE userId = :userId ORDER BY dateTime DESC")
    suspend fun getLogsForUser(userId: String): List<AccessLog>
}
