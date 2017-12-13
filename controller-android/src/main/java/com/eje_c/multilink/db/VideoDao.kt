package com.eje_c.multilink.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.os.SystemClock
import com.eje_c.multilink.UPDATE_TIME_THRESHOLD_FOR_CLEAR
import com.eje_c.multilink.UPDATE_TIME_THRESHOLD_MILLIS

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(videoEntity: List<VideoEntity>)

    @Query("SELECT *, COUNT(*) as count FROM VideoEntity WHERE updated_at > :updateTimeThreshold GROUP BY path HAVING count = (SELECT COUNT(*) FROM DeviceEntity WHERE updated_at > :updateTimeThreshold)")
    fun query(updateTimeThreshold: Long = SystemClock.uptimeMillis() - UPDATE_TIME_THRESHOLD_MILLIS): LiveData<List<VideoEntity>>

    @Query("DELETE FROM VideoEntity WHERE updated_at < :updateTimeThreshold")
    fun clear(updateTimeThreshold: Long = SystemClock.uptimeMillis() - UPDATE_TIME_THRESHOLD_FOR_CLEAR)
}