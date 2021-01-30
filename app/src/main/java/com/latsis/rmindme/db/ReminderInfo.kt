package com.latsis.rmindme.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminderInfo")
data class ReminderInfo (
    @PrimaryKey(autoGenerate = true) var uid:Int,
    @ColumnInfo(name="username") var username:String,
    @ColumnInfo(name="title")  var title:String,
    @ColumnInfo(name="description") var description:String,
    @ColumnInfo(name="date") var date: String
)
