package com.eje_c.multilink.data

import com.eje_c.multilink.JSON

class Message(val type: Int, val data: Any? = null) {

    fun serialize(): ByteArray {
        return JSON.stringify(this).toByteArray()
    }

}