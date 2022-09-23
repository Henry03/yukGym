package com.example.yukgym.room

import androidx.room.*

@Dao
interface ScheduleDao {

    @Insert
    fun addSchedule(schedule: Schedule)

    @Update
    fun updateSchedule(schedule: Schedule)

    @Delete
    fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedule")
    fun getSchedule() : List<Schedule>

    @Query("SELECT * FROM register WHERE id =:Schedule_id")
    fun getschedule(Schedule_id: Int) : Schedule
}