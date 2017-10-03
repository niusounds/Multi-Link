package com.eje_c.udpmultiview.data

import com.eje_c.udpmultiview.JSON

class Message<out T>(val type: Int, val data: T) {

    fun serialize(): ByteArray {
        val json = JSON.stringify(this)
        return json.toByteArray()
    }
}