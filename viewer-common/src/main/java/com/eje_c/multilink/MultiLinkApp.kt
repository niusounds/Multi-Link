package com.eje_c.multilink

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.eje_c.multilink.data.DeviceInfo
import com.eje_c.multilink.udp.MultiLinkUdpMessenger
import java.net.SocketAddress
import java.util.*

/**
 * アプリケーションのメインクラス。
 */
class MultiLinkApp(val context: Context, val main: IMain) {

    /**
     * trueの時かつUDP送信先がわかっている場合はヘッドトラッキング情報を毎フレーム送信する。
     */
//    var sendHeadTransform: Boolean = false

    init {
        // Set handler
        MultiLinkUdpMessenger.onReceivePing += ::respondWithMyDeviceInfo
        MultiLinkUdpMessenger.onReceiveControlMessage += main::updateState
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    fun updateHeadOrientation(x: Float, y: Float, z: Float, w: Float) {

        // ヘッドトラッキング情報の送信
//        if (sendHeadTransform) {
//            udpSender.send(x, y, z, w)
//        }
    }

    /**
     * 端末情報をコントローラーに送る。
     */
    private fun respondWithMyDeviceInfo(remote: SocketAddress) {

        // ランダムでディレイを入れてから送り返す
        Handler(Looper.getMainLooper()).postDelayed({
            main.runOnGlThread {
                MultiLinkUdpMessenger.sendDeviceInfo(DeviceInfo.get(context), remote)
            }
        }, Random().nextInt(3000).toLong())
    }
}
