package com.reihan.smartalarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.reihan.smartalarm.data.Alarm

@Database(entities = [Alarm::class], version = 2)
abstract class AlarmDB: RoomDatabase() {
    abstract fun alarmDao(): AlarmDAO

    companion object{
        @Volatile
        var instace: AlarmDB? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instace ?: synchronized(LOCK) {
            instace ?: buildDataBase(context).also {
                instace = it
            }
        }

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(context, AlarmDB::class.java, "smart_alarm.db")
                .fallbackToDestructiveMigration()
                .build()
    }

}