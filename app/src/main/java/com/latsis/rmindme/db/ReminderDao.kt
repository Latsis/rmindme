package com.latsis.rmindme.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ReminderDao {
    @Transaction
    @Insert
    fun insert(reminderInfo: ReminderInfo): Long

    @Query("DELETE FROM reminderInfo WHERE uid = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM reminderInfo")
    fun getReminderInfos(): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE uid = :id")
    fun getReminderInfo(id: Int): ReminderInfo

    @Update
    fun updateReminderInfo(vararg reminderInfo: ReminderInfo)

}