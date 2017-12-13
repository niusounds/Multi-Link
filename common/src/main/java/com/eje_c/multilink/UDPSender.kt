package com.eje_c.multilink

import com.eje_c.multilink.data.Message
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

/**
 * UDP
 */
class UDPSender(
        private val channel: DatagramChannel = DatagramChannel.open(),
        bufferSize: Int = 128 * 1024
) {

    /**
     * 最後にUDPメッセージを受信した時の相手アドレスを保持する。
     * ヘッドトラッキングや端末情報はここ宛に送られる。
     */
    var remote: SocketAddress? = null

    /**
     * UDP送信時に使用するバッファ。このバッファへのアクセスは必ずGLスレッドから行う必要がある。
     */
    private val buffer = ByteBuffer.allocate(bufferSize)

    init {
        channel.configureBlocking(false)
    }

    /**
     * ヘッドトラッキング情報を送信する。
     */
    fun send(q: FloatArray) {
        if (remote != null) {
            buffer.clear()

            val f = buffer.asFloatBuffer()
            f.put(q[0])
            f.put(q[1])
            f.put(q[2])
            f.put(q[3])
            f.flip()

            channel.send(buffer, remote)
        }
    }

    /**
     * ヘッドトラッキング情報を送信する。
     */
    fun send(x: Float, y: Float, z: Float, w: Float) {
        if (remote != null) {
            buffer.clear()

            val f = buffer.asFloatBuffer()
            f.put(x)
            f.put(y)
            f.put(z)
            f.put(w)
            f.flip()

            channel.send(buffer, remote)
        }
    }

    fun send(message: Message<*>) {
        if (remote != null) {
            buffer.clear()

            buffer.put(message.serialize())
            buffer.flip()

            channel.send(buffer, remote)
        }
    }
}