package com.eje_c.udpmultiview.gearvr

import com.eje_c.udpmultiview.BasePlayer
import com.eje_c.udpmultiview.R
import com.eje_c.udpmultiview.data.ControlMessage
import org.meganekkovr.Entity
import org.meganekkovr.Scene
import org.meganekkovr.SurfaceRendererComponent

/**
 * 動画再生を行うシーン。
 */
class PlayerScene : Scene() {

    private val player = BasePlayer()
    private lateinit var waiting: Entity
    private lateinit var screen: Entity

    /**
     * シーンの初期化を行う。
     */
    override fun init() {
        super.init()

        // Get Entity
        waiting = findById(R.id.waiting)!!
        screen = findById(R.id.screen)!!

        // シーン中の球にMediaPlayerの映像を送るようにする
        val surfaceRenderer = SurfaceRendererComponent()
        surfaceRenderer.setContinuousUpdate(true)
        screen.add(surfaceRenderer)
        player.mediaPlayer.setSurface(surfaceRenderer.surface)

        player.onStartPlaying = {
            screen.isVisible = true
            waiting.isVisible = false
        }

        player.onStopPlaying = {
            screen.isVisible = false
            waiting.isVisible = true
        }

        player.onLoaded = {

            // 1:1のサイズならTOP-BOTTOMの3Dとして扱う。そうでなければ2Dとして扱う。
            if (player.mediaPlayer.videoWidth == player.mediaPlayer.videoHeight) {
                screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.TOP_BOTTOM
            } else {
                screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.NORMAL
            }
        }
    }

    /**
     * プレイヤーの状態を更新する。
     */
    fun updateState(newControlMessage: ControlMessage) {
        player.updateState(newControlMessage)
    }
}
