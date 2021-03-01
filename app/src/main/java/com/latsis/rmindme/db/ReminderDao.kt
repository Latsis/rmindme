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

    @Query("SELECT * FROM reminderInfo WHERE creator_id = :creatorId")
    fun getUserReminderInfos(creatorId: String): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE creator_id = :creatorId AND reminder_seen = \"1\"")
    fun getPreviousUserReminderInfos(creatorId: String): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE creator_id = :creatorId AND reminder_seen != \"1\"")
    fun getUpcomingUserReminderInfos(creatorId: String): List<ReminderInfo>

    @Query("SELECT * FROM reminderInfo WHERE uid = :id")
    fun getReminderInfo(id: Int): ReminderInfo

    @Update
    fun updateReminderInfo(vararg reminderInfo: ReminderInfo)

    @Query("UPDATE ReminderInfo SET reminder_seen = :seenOrNot WHERE uid = :id")
    fun updateReminderSeen(seenOrNot: String, id: Int)

    @Query("UPDATE ReminderInfo SET reminder_time = :reminderTimeEdit WHERE uid = :id")
    fun updateReminderTime(reminderTimeEdit: String, id: Int)

}