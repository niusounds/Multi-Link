package com.eje_c.udpmultiview.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.os.SystemClock
import com.eje_c.udpmultiview.UPDATE_TIME_THRESHOLD_MILLIS

@Dao
interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(deviceEntity: DeviceEntity)

    @Query("SELECT * FROM DeviceEntity WHERE updated_at > :updateTimeThreshold")
    fun query(updateTimeThreshold: Long = SystemClock.uptimeMillis() - UPDATE_TIME_THRESHOLD_MILLIS): LiveData<List<DeviceEntity>>

    @Query("DELETE FROM DeviceEntity WHERE updated_at > :updateTimeThreshold")
    fun clear(updateTimeThreshold: Long = SystemClock.uptimeMillis() - UPDATE_TIME_THRESHOLD_MILLIS)

}