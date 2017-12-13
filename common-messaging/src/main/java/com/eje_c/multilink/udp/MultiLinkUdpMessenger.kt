package com.eje_c.multilink.udp

import android.util.Log
import com.eje_c.multilink.data.ControlMessage
import com.eje_c.multilink.data.DeviceInfo
import com.eje_c.multilink.data.Message
import com.eje_c.multilink.json.JSON
import org.json.JSONObject
import java.net.SocketAddress

/**
 * High level interface for Multi-Link UDP messaging.
 */
object MultiLinkUdpMessenger {

    private const val TAG = "MultiLinkUdpMessenger"

    private lateinit var udpSocket: UdpSocket

    /**
     * Callbacks for ping message { type: 0 }
     */
    val onReceivePing = mutableSetOf<(SocketAddress) -> Unit>()

    /**
     * Callback for ping result { type: 0, data: { ... } }
     */
    val onReceivePingResponse = mutableSetOf<(DeviceInfo) -> Unit>()

    /**
     * Callback for control message { type: 1, data: { ... } }
     */
    val onReceiveControlMessage = mutableSetOf<(ControlMessage) -> Unit>()

    val broadcastPort: Int = 50201

    /**
     * Call this before any methods.
     */
    fun initialize(udpSocket: UdpSocket) {
        this.udpSocket = udpSocket

        // On receive UDP data
        udpSocket.onReceive = { data, remote ->

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
                        onReceivePing.forEach { it(remote) }
                    }
                }
            // Control message
                1 -> {
                    if (json.has("data")) {
                        val controlMessage = JSON.parse<ControlMessage>(json.getJSONObject("data").toString())
                        onReceiveControlMessage.forEach { it(controlMessage) }
                    }
                }
            }
        }
    }

    /**
     * Send ping message.
     */
    fun ping() {

        val data = Message(type = 0).serialize()

        udpSocket.broadcast(data, broadcastPort)
    }

    /**
     * Send device information.
     */
    fun sendDeviceInfo(deviceInfo: DeviceInfo, remote: SocketAddress) {

        val message = Message(0, deviceInfo).serialize()

        udpSocket.send(message, remote)
    }

    /**
     * Send control message.
     */
    fun sendControlMessage(controlMessage: ControlMessage) {

        val data = Message(type = 1, data = controlMessage).serialize()

        udpSocket.broadcast(data, broadcastPort)
    }

    /**
     * Close UDP socket.
     */
    fun release() {
        udpSocket.release()
    }

}