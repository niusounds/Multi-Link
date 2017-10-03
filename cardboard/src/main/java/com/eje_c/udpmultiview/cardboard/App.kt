package com.eje_c.udpmultiview.cardboard

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.eje_c.udpmultiview.ControlMessageReceiveEvent
import com.eje_c.udpmultiview.PlayerScene
import com.eje_c.udpmultiview.UDPReceiverService
import com.eje_c.udpmultiview.UDPSender
import com.eje_c.udpmultiview.data.ControlMessage
import com.eje_c.udpmultiview.data.DeviceInfo
import com.eje_c.udpmultiview.data.Message
import com.eje_c.udpmultiview.data.Type
import com.google.vr.sdk.base.HeadTransform
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class App(context: Context) : VRRenderer(context) {

    private lateinit var udpSender: UDPSender
    private val quaternion = FloatArray(4)

    /**
     * trueの時かつUDP送信先がわかっている場合はヘッドトラッキング情報を毎フレーム送信する。
     */
    var sendHeadTransform: Boolean = false

    /**
     * アプリケーション開始時に呼ばれる。
     */
    override fun init() {

        // シーンを作成
        val playerScene = PlayerScene(this)
        addAndSwitchScene(playerScene)

        // UDPReceiverServiceからのメッセージを受け取る準備
        EventBus.getDefault().register(this)

        // UDP送信準備
        udpSender = UDPSender()
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun onNewFrame(headTransform: HeadTransform) {

        // ヘッドトラッキング情報の送信
        if (sendHeadTransform) {
            headTransform.getQuaternion(quaternion, 0)
            udpSender.send(quaternion)
        }

        super.onNewFrame(headTransform)
    }

    /**
     * [UDPReceiverService]からメッセージを受け取った時に呼ばれる。
     */
    @Subscribe
    fun onReceiveState(event: ControlMessageReceiveEvent) {

        // 親機のアドレスを保存
        udpSender.remote = event.remote

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
                udpSender.send(message)
            }
        }, Random().nextInt(3000).toLong())
    }

    /**
     * プレイヤーの状態を更新する。
     */
    private fun updatePlayerState(event: ControlMessageReceiveEvent) {
        val scene = currentScene
        if (scene is PlayerScene) {
            val data = event.message.getJSONObject("data")
            val control = ControlMessage(data.getString("path"), data.getBoolean("playing"), data.getInt("position"))
            scene.updateState(control)
        }
    }

    /**
     * ヘッドトラッキング情報を送るかどうかを設定する。
     */
    private fun updateSendHeadTrackingFlag(event: ControlMessageReceiveEvent) {
        sendHeadTransform = event.message.getBoolean("data")
    }
}
