package com.latsis.rmindme.db

import androidx.room.*

@Dao
interface UserDao {
    @Transaction
    @Insert
    fun insert(userInfo: UserInfo): Long

    @Query("SELECT * FROM userInfo")
    fun getAll(): List<UserInfo>

    @Query("SELECT * FROM userInfo WHERE username = :name")
    fun findIfExists(name: String): Boolean

    @Query("SELECT * FROM userInfo WHERE username = :name")
    fun findByName(name: String): UserInfo

    @Insert
    fun insertAll(vararg users: UserInfo)

    @Delete
    fun delete(user: UserInfo)
}