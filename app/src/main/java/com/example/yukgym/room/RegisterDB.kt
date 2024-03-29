package com.example.yukgym.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Register::class, Schedule::class],
    version = 1
)

abstract class RegisterDB : RoomDatabase() {

    abstract fun registerDao() : RegisterDao
    abstract fun scheduleDao() : ScheduleDao

    companion object{
        @Volatile private var instance : RegisterDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?: buildDatabase(context).also{
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            RegisterDB::class.java,
            "register12345.db"
            ).allowMainThreadQueries().build()

    }

}
