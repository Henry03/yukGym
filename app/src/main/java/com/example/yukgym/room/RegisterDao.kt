package com.example.yukgym.room

import androidx.room.*

@Dao
interface RegisterDao {

    @Insert
    suspend fun addRegister(register: Register)

    @Update
    suspend fun updateNote(register: Register)

    @Delete
    suspend fun deleteNote(register: Register)

    @Query("SELECT * FROM register")
    suspend fun getRegister() : List<Register>

    @Query("SELECT * FROM register WHERE id =:register_id")
    suspend fun getRegister(register_id: String) : List<Register>
}