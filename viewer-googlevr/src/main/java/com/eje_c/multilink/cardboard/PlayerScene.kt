package com.eje_c.multilink.cardboard

import com.eje_c.multilink.BasePlayer
import com.eje_c.multilink.data.ControlMessage
import org.rajawali3d.Object3D
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.scene.Scene

/**
 * 動画再生を行うシーン。
 */
class PlayerScene(renderer: Renderer) : Scene(renderer) {

    private val player = BasePlayer(renderer.context)
    private var waiting: Object3D
    private var screen: VRSphere

    /**
     * シーンの初期化を行う。
     */
    init {
        waiting = ViewUtil.toObject3D(mRenderer.context, R.layout.waiting, "waiting")
        waiting.position = Vector3(0.0, 0.0, -10.0)
        addChild(waiting)
        screen = VRSphere()
        screen.isVisible = false
        addChild(screen)

        screen.setSurfaceListener({ surface -> player.setSurface(surface) })

        player.onStartPlaying = {
            screen.isVisible = true
            waiting.isVisible = false
        }

        player.onStopPlaying = {
            screen.isVisible = false
            waiting.isVisible = true
        }
    }

    /**
     * プレイヤーの状態を更新する。
     */
    fun updateState(newControlMessage: ControlMessage) {
        player.updateState(newControlMessage)
    }
}
