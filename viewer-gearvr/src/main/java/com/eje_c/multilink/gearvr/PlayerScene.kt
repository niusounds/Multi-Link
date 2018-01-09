package com.eje_c.multilink.gearvr

import com.eje_c.multilink.BasePlayer
import com.eje_c.multilink.data.ControlMessage
import org.meganekkovr.*

/**
 * 動画再生を行うシーン。
 */
class PlayerScene : Scene() {

    private lateinit var player: BasePlayer
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

        // BasePlayer must be created in main thread
        app.runOnUiThread {
            player = BasePlayer(app.context)

            app.runOnGlThread {

                player.onStartPlaying = {
                    screen.isVisible = true
                    waiting.isVisible = false
                }

                player.onStopPlaying = {
                    screen.isVisible = false
                    waiting.isVisible = true
                }

                player.onLoaded = {

                    if (player.isStereo) {
                        screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.TOP_BOTTOM
                    } else {
                        screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.NORMAL
                    }
                }

                // シーン中の球にMediaPlayerの映像を送るようにする
                val surfaceRenderer = SurfaceRendererComponent()
                surfaceRenderer.setContinuousUpdate(true)
                screen.add(surfaceRenderer)
                player.setSurface(surfaceRenderer.surface)
            }
        }

    }

    override fun update(frame: FrameInput) {

        if (this::player.isInitialized) {
            val headOrientation = HeadTransform.getInstance().quaternion
            player.updateHeadOrientation(headOrientation.w, headOrientation.x, headOrientation.y, headOrientation.z)
        }

        super.update(frame)
    }

    /**
     * プレイヤーの状態を更新する。
     */
    fun updateState(newControlMessage: ControlMessage) {
        if (this::player.isInitialized) {
            player.updateState(newControlMessage)
        }
    }
}
