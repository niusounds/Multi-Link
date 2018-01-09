package com.eje_c.multilink.cardboard

import android.content.Context
import com.eje_c.multilink.IMain
import com.eje_c.multilink.MultiLinkApp
import com.eje_c.multilink.data.ControlMessage
import com.google.vr.sdk.base.HeadTransform

class App(context: Context) : VRRenderer(context), IMain {

    private lateinit var app: MultiLinkApp
    private lateinit var playerScene: PlayerScene
    private val quaternion = FloatArray(4)

    /**
     * アプリケーション開始時に呼ばれる。
     */
    override fun init() {

        app = MultiLinkApp(context, this)

        // シーンを作成
        playerScene = PlayerScene(this)
        addAndSwitchScene(playerScene)
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun onNewFrame(headTransform: HeadTransform) {

        // ヘッドトラッキング情報の送信
        headTransform.getQuaternion(quaternion, 0)
        app.updateHeadOrientation(quaternion[0], quaternion[1], quaternion[2], quaternion[3])
        playerScene.updateHeadOrientation(quaternion[3], quaternion[0], quaternion[1], quaternion[2])

        super.onNewFrame(headTransform)
    }

    override fun updateState(controlMessage: ControlMessage) {
        playerScene.updateState(controlMessage)
    }
}
