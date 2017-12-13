package com.eje_c.udpmultiview.controller

import android.annotation.SuppressLint
import android.net.Uri
import android.os.SystemClock
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.eje_c.udpmultiview.data.ControlMessage
import com.eje_c.udpmultiview.fromExternal
import com.eje_c.udpmultiview.udp.MultiViewUdpMessenger
import com.google.vr.sdk.widgets.video.VrVideoEventListener
import kotlinx.android.synthetic.main.activity_player.*
import org.androidannotations.annotations.*
import org.androidannotations.api.BackgroundExecutor

/**
 * Created by niuso on 2017/12/12.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_player)
open class PlayerActivity : AppCompatActivity() {

    private var playInController: Boolean = false

    /**
     * Time when [playing] is set to true.
     */
    private var playTime: Long = 0

    /**
     * Message which is sent to VR devices.
     */
    private var controlMessage = ControlMessage()

    /**
     * Get/Set current position in millis. Must be accessed from main thread.
     */
    var currentPosition: Long
        get() = controlMessage.position
        set(value) {

            // Sync to VR devices
            controlMessage.position = value
            MultiViewUdpMessenger.sendControlMessage(controlMessage)

            if (playInController) {
                vrVideoView.seekTo(value)
            }

            seekBar.progress = value.toInt()
        }

    /**
     * Get/Set playing state. Must be accessed from main thread.
     */
    var playing: Boolean
        get() = controlMessage.playing
        set(value) {

            // Sync to VR devices
            controlMessage.playing = value
            MultiViewUdpMessenger.sendControlMessage(controlMessage)

            if (value) {
                vrVideoView.playVideo()
                playPauseButton.setImageResource(R.drawable.ic_pause)
                playTime = SystemClock.uptimeMillis()
            } else {
                vrVideoView.pauseVideo()
                playPauseButton.setImageResource(R.drawable.ic_play)
            }

        }

    @Extra
    @JvmField
    var videoPath: String = "Oculus/360Videos/video.mp4"
    @Extra
    @JvmField
    var videoLength: Long = 0

    @ViewById
    lateinit var playPauseButton: FloatingActionButton

    @AfterViews
    fun init() {

        // Reset VR devices state
        controlMessage.path = videoPath
        MultiViewUdpMessenger.sendControlMessage(controlMessage)

        seekBar.max = videoLength.toInt()

        // Check if local video exists
        val localVideo = fromExternal(videoPath)
        if (localVideo.exists()) {

            // Load local video
            vrVideoView.loadVideo(Uri.fromFile(localVideo), null)
            vrVideoView.pauseVideo()

            // Init UI
            vrVideoView.setFullscreenButtonEnabled(false)
            vrVideoView.setInfoButtonEnabled(false)
            vrVideoView.setStereoModeButtonEnabled(false)

            // Notify when playing video is complete
            vrVideoView.setEventListener(object : VrVideoEventListener() {
                override fun onCompletion() {
                    playing = false
                    currentPosition = 0
                }
            })

            // Hide "cannot play" text
            cannotPlayOnController.visibility = View.GONE
            playInController = true

        } else {

            vrVideoView.visibility = View.GONE
            playInController = false

        }
    }

    override fun onResume() {
        super.onResume()
        vrVideoView.resumeRendering()

        periodicUiUpdate()
    }

    override fun onPause() {
        vrVideoView.pauseRendering()
        BackgroundExecutor.cancelAll("periodicUiUpdate", true)

        // Pause all devices
        playing = false

        super.onPause()
    }

    override fun onDestroy() {
        vrVideoView.shutdown()
        super.onDestroy()
    }

    @Click
    fun playPauseButtonClicked() {

        // Toggle playing state
        playing = !playing
    }

    @Background(id = "periodicUiUpdate")
    open fun periodicUiUpdate() {


        try {

            var prevNow = SystemClock.uptimeMillis()

            while (!Thread.interrupted()) {

                val now = SystemClock.uptimeMillis()
                val dt = now - prevNow

                if (playInController) {

                    controlMessage.position = vrVideoView.currentPosition

                } else {

                    if (controlMessage.playing) {
                        controlMessage.position += dt

                        // Stop playing when position is over than length
                        if (controlMessage.position > videoLength) {
                            pause()
                            seekTo(0)
                        }
                    }

                }

                updateSeekBar()
                MultiViewUdpMessenger.sendControlMessage(controlMessage)

                prevNow = now

                Thread.sleep(200)
            }

        } catch (e: InterruptedException) {
            Log.d(TAG, "Background thread is stopped.")
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    open fun pause() {
        playing = false
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    open fun play() {
        playing = true
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    open fun seekTo(position: Long) {
        currentPosition = position
    }

    @UiThread
    open fun updateSeekBar() {

        seekBar.progress = controlMessage.position.toInt()

    }

    @SeekBarProgressChange(R.id.seekBar)
    fun onSeekBarChange(progress: Int, fromUser: Boolean) {
        if (fromUser) {
            currentPosition = progress.toLong()
        }
    }

    companion object {
        const val TAG = "PlayerActivity"
    }
}