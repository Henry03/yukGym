package com.example.yukgym.room


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Register (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val notelp: String,
    val email: String,
    val birthdate: String,
    val password: String
){

}