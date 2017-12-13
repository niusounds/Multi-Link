package com.eje_c.multilink.data

import com.eje_c.multilink.JSON

class Message<out T>(val type: Int, val data: T) {

    fun serialize(): ByteArray {
        val json = JSON.stringify(this)
        return json.toByteArray()
    }
}