package com.example.yukgym.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (
    @PrimaryKey(autoGenerate = true)
    val Name: String,
    val NOTelp: String,
    val Email: String,
    val BirthDate: String,
    val Password: String,
    val PasswordConfirm: String
)
