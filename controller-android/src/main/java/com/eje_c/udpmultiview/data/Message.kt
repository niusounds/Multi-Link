package com.eje_c.udpmultiview.data

import com.eje_c.udpmultiview.JSON

class Message(val type: Int, val data: Any? = null) {

    fun serialize(): ByteArray {
        return JSON.stringify(this).toByteArray()
    }

}