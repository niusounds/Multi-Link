package com.eje_c.udpmultiview.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.eje_c.udpmultiview.db.DeviceDao
import com.eje_c.udpmultiview.db.DeviceEntity
import com.eje_c.udpmultiview.db.VideoDao
import com.eje_c.udpmultiview.db.VideoEntity

/**
 * Database for app. Implementation is generated with Room library.
 */
@Database(entities = [DeviceEntity::class, VideoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    abstract fun videoDao(): VideoDao

}