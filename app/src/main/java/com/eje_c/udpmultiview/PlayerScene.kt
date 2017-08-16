package com.eje_c.udpmultiview

import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import com.eje_c.udpmultiview.data.ControlMessage
import org.meganekkovr.Entity
import org.meganekkovr.Scene
import org.meganekkovr.SurfaceRendererComponent
import java.io.File

/**
 * 動画再生を行うシーン。
 */
class PlayerScene : Scene() {

    private val TAG = "PlayerScene"
    private val SEEK_THRESHOLD = 1000 // コントロールメッセージのpositionプロパティと現在位置がこの値以上離れていたらシークする

    private val mediaPlayer = MediaPlayer()
    private lateinit var waiting: Entity
    private lateinit var screen: Entity
    private var currentPath: String? = null

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
        mediaPlayer.setSurface(surfaceRenderer.surface)

        // 再生終了したら待機画面
        mediaPlayer.setOnCompletionListener {
            hideScreen()
        }
    }

    /**
     * プレイヤーの状態を更新する。
     */
    fun updateState(newControlMessage: ControlMessage) {
        Log.d(TAG, "updateState $newControlMessage")

        // 読み込んでいるパスと異なるパスを受け取ったら、読み込む
        var isNewVideo = false
        if (currentPath != newControlMessage.path) {
            try {
                load(newControlMessage.path)
                isNewVideo = true
            } catch (e: Exception) {
                Log.e(TAG, "Loading error", e)
                return
            }
        }

        // 現在の再生状態と異なるメッセージを受け取ったら、再生状態を更新する
        if (mediaPlayer.isPlaying != newControlMessage.playing) {
            if (newControlMessage.playing) {
                play(isNewVideo)
            } else {
                pause()
            }
        }

        // 現在位置より一定以上離れた再生位置を受け取ったら、シークする
        if ((Math.abs(mediaPlayer.currentPosition - newControlMessage.position) > SEEK_THRESHOLD)) {
            seekTo(newControlMessage.position)
        }
    }

    /**
     * シークする。
     */
    private fun seekTo(position: Int) {
        Log.d(TAG, "seek to $position ${mediaPlayer.currentPosition}")

        mediaPlayer.seekTo(position)
    }

    /**
     * 再生開始する。
     */
    private fun play(waitForFirstFrame: Boolean) {
        Log.d(TAG, "play $waitForFirstFrame")

        // 再生中でなければ再生開始
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        if (waitForFirstFrame) {

            // 動画の最初のフレームをレンダリングするタイミングで表示を切り替える
            mediaPlayer.setOnInfoListener { mediaPlayer, what, extra ->

                when (what) {
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                        showScreen()
                        mediaPlayer.setOnInfoListener(null)
                        return@setOnInfoListener true
                    }
                }

                return@setOnInfoListener false
            }

        } else {
            // すぐに表示を切り替える
            showScreen()
        }
    }

    /**
     * スクリーンを表示する。
     */
    private fun showScreen() {
        screen.isVisible = true
        waiting.isVisible = false
    }

    /**
     * 待機表示にする。
     */
    private fun hideScreen() {
        screen.isVisible = false
        waiting.isVisible = true
    }

    /**
     * 一時停止する。
     */
    private fun pause() {
        Log.d(TAG, "pause")

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }

        hideScreen()
    }

    /**
     * 指定した外部ストレージパスからデータを読み込む。
     */
    private fun load(path: String) {
        Log.d(TAG, "load $path")
        this.currentPath = path

        // 読み込み始める前に待機表示にする
        hideScreen()

        // 読み込む
        mediaPlayer.reset()
        mediaPlayer.setDataSource(File(Environment.getExternalStorageDirectory(), path).absolutePath)
        mediaPlayer.prepare()

        // 最初のフレームをデコードしてすぐ再生できるようにする
        mediaPlayer.seekTo(0)

        // 1:1のサイズならTOP-BOTTOMの3Dとして扱う。そうでなければ2Dとして扱う。
        if (mediaPlayer.videoWidth == mediaPlayer.videoHeight) {
            screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.TOP_BOTTOM
        } else {
            screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.NORMAL
        }
    }
}
