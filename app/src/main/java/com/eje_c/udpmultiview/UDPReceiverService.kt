package com.eje_c.udpmultiview

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * This service listens UDP messages from anywhere to port 50201.
 */
class UDPReceiverService : Service() {

    private val TAG = "UDPReceiverService"
    private val executorService = Executors.newSingleThreadExecutor()
    private var future: Future<*>? = null
    private var channel: DatagramChannel? = null
    private lateinit var buffer: ByteBuffer

    override fun onBind(intent: Intent): IBinder? {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        try {
            channel = DatagramChannel.open()
            channel!!.socket().bind(InetSocketAddress(50201))
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
            return
        }

        buffer = ByteBuffer.allocate(256)

        // Execute UDP listening thread
        future = executorService.submit {
            while (true) {
                buffer.clear()

                try {
                    // Receive UDP packet
                    val socketAddress = channel!!.receive(buffer)
                    buffer.flip()

                    // Copy to byte[] and convert it to String
                    val data = ByteArray(buffer.limit())
                    buffer.get(data)

                    val jsonStr = String(data)
                    val json = JSONObject(jsonStr)
                    EventBus.getDefault().post(ControlMessageReceiveEvent(json, socketAddress))

                } catch (e: JSONException) {
                    Log.e(TAG, "Invalid message was received.", e);
                } catch (e: IOException) {
                    Log.e(TAG, "Receiving error.", e);
                    break
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        future?.cancel(true)
        future = null

        try {
            channel?.close()
        } catch (e: IOException) {
        }

        channel = null
    }
}
