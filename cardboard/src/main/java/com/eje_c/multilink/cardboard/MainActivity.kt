package com.eje_c.multilink.cardboard

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.eje_c.multilink.UDPReceiverService
import com.google.vr.sdk.base.GvrActivity
import com.google.vr.sdk.base.GvrView

class MainActivity : GvrActivity() {

    private val TAG = "MainActivity"
    private lateinit var app: App

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d(TAG, "onServiceConnected: ")
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // レンダリング準備
        val surfaceView = GvrView(this)
        surfaceView.stereoModeEnabled = false
        setContentView(surfaceView)

        // アプリケーションを作成
        app = App(this)
        surfaceView.setRenderer(app)

        // Activity開始と同時にUDP受信サービスを開始。終了時に停止する。
        bindService(Intent(this, UDPReceiverService::class.java), conn, BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        app.onResume()
    }

    override fun onPause() {
        super.onPause()
        app.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        app.onDestroy()
        unbindService(conn)
    }
}
