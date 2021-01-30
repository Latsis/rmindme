package com.latsis.rmindme.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ReminderInfo::class, UserInfo::class), version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
   abstract fun reminderDao(): ReminderDao
   abstract fun userDao(): UserDao
}