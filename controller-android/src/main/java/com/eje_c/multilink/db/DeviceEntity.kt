package com.eje_c.multilink.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class DeviceEntity {
    @PrimaryKey
    var imei: String = ""
    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long = 0
}