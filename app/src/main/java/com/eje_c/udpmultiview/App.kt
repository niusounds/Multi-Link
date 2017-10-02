package com.eje_c.udpmultiview

import android.os.Handler
import android.os.Looper
import com.eje_c.udpmultiview.data.ControlMessage
import com.eje_c.udpmultiview.data.DeviceInfo
import com.eje_c.udpmultiview.data.Message
import com.eje_c.udpmultiview.data.Type
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.meganekkovr.FrameInput
import org.meganekkovr.HeadTransform
import org.meganekkovr.MeganekkoApp
import java.util.*

/**
 * アプリケーションのメインクラス。
 */
class App : MeganekkoApp() {

    private lateinit var udpSender: UDPSender

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
        udpSender = UDPSender()
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun update(frame: FrameInput) {

        // ヘッドトラッキング情報の送信
        if (sendHeadTransform) {
            udpSender.send(HeadTransform.getInstance().quaternion)
        }

        super.update(frame)
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
