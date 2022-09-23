package com.example.yukgym.room


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Schedule (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val date: String,
    val activity: String
){

}