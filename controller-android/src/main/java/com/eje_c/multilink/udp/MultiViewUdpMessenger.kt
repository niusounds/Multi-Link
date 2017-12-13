package com.eje_c.multilink.udp

import android.util.Log
import com.eje_c.multilink.JSON
import com.eje_c.multilink.data.ControlMessage
import com.eje_c.multilink.data.DeviceInfo
import com.eje_c.multilink.data.Message
import org.json.JSONObject

/**
 * High level interface for MultiView App.
 */
object MultiViewUdpMessenger {

    const val TAG = "MultiViewUdpMessenger"

    private lateinit var udpSocket: UdpSocket

    /**
     * Callbacks for ping message { type: 0 }
     */
    val onReceivePing = mutableSetOf<() -> Unit>()

    /**
     * Callback for ping result { type: 0, data: { ... } }
     */
    val onReceivePingResponse = mutableSetOf<(DeviceInfo) -> Unit>()

    val broadcastPort: Int = 50201

    /**
     * Call this before any methods.
     */
    fun initialize(udpSocket: UdpSocket) {
        this.udpSocket = udpSocket

        // On receive UDP data
        udpSocket.onReceive = { data ->

            // Decode bytes to string
            val bytes = ByteArray(data.limit())
            data.get(bytes)
            val str = String(bytes)

            Log.d(TAG, "Receive $str")

            // Parse as JSON
            val json = JSONObject(str)

            when (json.getInt("type")) {
            // Ping result
                0 -> {
                    if (json.has("data")) {
                        val deviceInfo = JSON.parse<DeviceInfo>(json.getJSONObject("data").toString())
                        onReceivePingResponse.forEach { it(deviceInfo) }
                    } else {
                        onReceivePing.forEach { it() }
                    }
                }
            }
        }
    }

    fun ping() {

        val data = Message(type = 0).serialize()

        udpSocket.broadcast(data, broadcastPort)
    }

    fun sendControlMessage(controlMessage: ControlMessage) {

        val data = Message(type = 1, data = controlMessage).serialize()

        udpSocket.broadcast(data, broadcastPort)
    }

    fun release() {
        udpSocket.release()
    }

}