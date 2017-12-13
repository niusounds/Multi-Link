package com.eje_c.multilink.data

import com.eje_c.multilink.JSON

/**
 * JSON object which is sent between VR devices and controller.
 */
class Message(val type: Int, val data: Any? = null) {

    /**
     * Convert to byte array for sending on network.
     */
    fun serialize(): ByteArray {
        return JSON.stringify(this).toByteArray()
    }

}