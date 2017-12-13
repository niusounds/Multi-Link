package com.eje_c.multilink.gearvr

import com.eje_c.multilink.IMain
import com.eje_c.multilink.MultiViewApp
import com.eje_c.multilink.data.ControlMessage
import org.meganekkovr.FrameInput
import org.meganekkovr.HeadTransform
import org.meganekkovr.MeganekkoApp

/**
 * アプリケーションのメインクラス。
 */
class App : MeganekkoApp(), IMain {

    private lateinit var app: MultiViewApp
    private lateinit var playerScene: PlayerScene

    /**
     * アプリケーション開始時に呼ばれる。
     */
    override fun init() {
        super.init()

        app = MultiViewApp(context, this)

        // シーンを読み込む
        playerScene = setSceneFromXml(R.xml.scene) as PlayerScene
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun update(frame: FrameInput) {

        // ヘッドトラッキング情報の送信
        val quaternion = HeadTransform.getInstance().quaternion
        app.updateHeadOrientation(quaternion.x, quaternion.y, quaternion.z, quaternion.w)

        super.update(frame)
    }

    override fun runOnGlThread(command: () -> Unit) = super.runOnGlThread(command)

    override fun updateState(controlMessage: ControlMessage) {
        playerScene.updateState(controlMessage)
    }
}
