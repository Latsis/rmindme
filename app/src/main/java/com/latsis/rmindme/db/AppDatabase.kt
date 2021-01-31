package com.latsis.rmindme.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ReminderInfo::class, UserInfo::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
   abstract fun reminderDao(): ReminderDao
   abstract fun userDao(): UserDao

   companion object {
      var INSTANCE: AppDatabase? = null

      fun getAppDatabase(context: Context): AppDatabase? {
         if (INSTANCE == null){
            synchronized(AppDatabase::class){
               INSTANCE = Room.databaseBuilder(
                       context.applicationContext,
                       AppDatabase::class.java,
                       "rmindme_database"
               ).build()
            }
         }
         return INSTANCE
      }

      fun destroyDataBase(){
         INSTANCE = null
      }
   }
}