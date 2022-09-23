package com.example.yukgym.room

import androidx.room.*

@Dao
interface RegisterDao {

    @Insert
    fun addRegister(register: Register)

    @Update
    fun updateNote(register: Register)

    @Delete
    fun deleteNote(register: Register)

    @Query("SELECT * FROM register")
    fun getRegister() : List<Register>

    @Query("SELECT * FROM register WHERE id =:register_id")
    fun getRegister(register_id: Int) : Register

    @Query("SELECT * FROM register WHERE name = :register_name and password = :register_password")
    fun getRegister(register_name: String, register_password: String) : Register
}