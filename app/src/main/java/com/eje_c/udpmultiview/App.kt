package com.eje_c.udpmultiview

import android.os.Handler
import android.os.Looper
import com.eje_c.udpmultiview.data.ControlMessage
import com.eje_c.udpmultiview.data.DeviceInfo
import com.eje_c.udpmultiview.data.Message
import com.eje_c.udpmultiview.data.Type
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.joml.Quaternionf
import org.meganekkovr.FrameInput
import org.meganekkovr.HeadTransform
import org.meganekkovr.MeganekkoApp
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.*

/**
 * アプリケーションのメインクラス。
 */
class App : MeganekkoApp() {

    /**
     * 最後にUDPメッセージを受信した時の相手アドレスを保持する。
     * ヘッドトラッキングや端末情報はここ宛に送られる。
     */
    private var remote: SocketAddress? = null

    /**
     * UDPソケット。
     */
    private lateinit var channel: DatagramChannel

    /**
     * UDP送信時に使用するバッファ。このバッファへのアクセスは必ずGLスレッドから行う必要がある。
     */
    private val buffer = ByteBuffer.allocate(128 * 1024)

    /**
     * trueの時かつUDP送信先がわかっている場合はヘッドトラッキング情報を毎フレーム送信する。
     */
    var sendHeadTransform: Boolean = false

    /**
     * アプリケーション開始時に呼ばれる。
     */
    override fun init() {
        super.init()

        // シーンを読み込む
        setSceneFromXml(R.xml.scene)

        // UDPReceiverServiceからのメッセージを受け取る準備
        EventBus.getDefault().register(this)

        // UDP送信準備
        channel = DatagramChannel.open()
        channel.configureBlocking(false)
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun update(frame: FrameInput) {

        // ヘッドトラッキング情報の送信
        if (sendHeadTransform && remote != null) {
            synchronized(this) {
                if (sendHeadTransform && remote != null) {
                    val q = HeadTransform.getInstance().quaternion
                    send(q)
                }
            }
        }

        super.update(frame)
    }

    /**
     * ヘッドトラッキング情報をコントローラーに送信する。
     */
    private fun send(q: Quaternionf) {
        buffer.clear()
        val f = buffer.asFloatBuffer()
        f.put(q.x)
        f.put(q.y)
        f.put(q.z)
        f.put(q.w)
        f.flip()
        channel.send(buffer, remote)
    }

    /**
     * [UDPReceiverService]からメッセージを受け取った時に呼ばれる。
     */
    @Subscribe
    fun onReceiveState(event: ControlMessageReceiveEvent) {

        // 親機のアドレスを保存
        synchronized(this) {
            remote = event.remote
        }

        // message.type によって処理を分岐
        when (Type.fromInt(event.message.getInt("type"))) {
        // 端末情報を送り返す
            Type.Ping -> respondWithMyDeviceInfo()
        // プレイヤーの状態を更新する
            Type.Control -> updatePlayerState(event)
        // ヘッドトラッキング情報の送信を切り替える
            Type.SendHeadTransform -> updateSendHeadTrackingFlag(event)
        }

    }

    /**
     * 端末情報をコントローラーに送る。
     */
    private fun respondWithMyDeviceInfo() {

        // ランダムでディレイを入れてから送り返す
        Handler(Looper.getMainLooper()).postDelayed({
            runOnGlThread {
                val message = Message(Type.Ping.value, DeviceInfo.get(context))
                buffer.clear()
                buffer.put(message.serialize())
                buffer.flip()
                channel.send(buffer, remote)
            }
        }, Random().nextInt(3000).toLong())
    }

    /**
     * プレイヤーの状態を更新する。
     */
    private fun updatePlayerState(event: ControlMessageReceiveEvent) {
        val currentScene = scene
        if (currentScene is PlayerScene) {
            val data = event.message.getJSONObject("data")
            val control = ControlMessage(data.getString("path"), data.getBoolean("playing"), data.getInt("position"))
            currentScene.updateState(control)
        }
    }

    /**
     * ヘッドトラッキング情報を送るかどうかを設定する。
     */
    private fun updateSendHeadTrackingFlag(event: ControlMessageReceiveEvent) {
        sendHeadTransform = event.message.getBoolean("data")
    }
}
