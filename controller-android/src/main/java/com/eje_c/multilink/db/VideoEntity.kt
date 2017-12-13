package com.eje_c.multilink.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity(primaryKeys = ["device_imei", "path"])
class VideoEntity {

    @ColumnInfo(name = "device_imei")
    var deviceImei: String = ""
    @ColumnInfo(name = "path")
    var path: String = ""

    @ColumnInfo(name = "name")
    var name: String? = null
    @ColumnInfo(name = "length")
    var length: Long = 0
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long = 0
}