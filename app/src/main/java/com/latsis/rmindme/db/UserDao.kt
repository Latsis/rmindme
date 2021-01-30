package com.latsis.rmindme.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface UserDao {
    @Query("SELECT * FROM userInfo")
    fun getAll(): List<UserInfo>

    @Query("SELECT * FROM userInfo WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<UserInfo>

    @Query("SELECT * FROM userInfo WHERE username LIKE :name LIMIT 1")
    fun findByName(name: String): UserInfo

    @Insert
    fun insertAll(vararg users: UserInfo)

    @Delete
    fun delete(user: UserInfo)
}