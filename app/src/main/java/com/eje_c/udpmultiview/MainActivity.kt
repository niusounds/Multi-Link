package com.eje_c.udpmultiview

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log

import org.meganekkovr.GearVRActivity

class MainActivity : GearVRActivity() {
    private val TAG = "MainActivity"

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

        // Activity開始と同時にUDP受信サービスを開始。終了時に停止する。
        bindService(Intent(this, UDPReceiverService::class.java), conn, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(conn)
        super.onDestroy()
    }
}
