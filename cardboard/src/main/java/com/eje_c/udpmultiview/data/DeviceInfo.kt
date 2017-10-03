package com.eje_c.udpmultiview.data

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import java.io.File

/**
 * コントローラーに送信する端末情報を表すクラス。
 */
data class DeviceInfo(
        val imei: String,
        val name: String,
        val videos: Array<VideoInfo>) {

    companion object {

        fun get(context: Context): DeviceInfo {

            val telephonyManager = context.getSystemService(TelephonyManager::class.java)
            val imei = telephonyManager.deviceId
            val videoList = getVRVideos(context)

            return DeviceInfo(imei, "${Build.BRAND}: ${Build.MODEL}", videoList)
        }

        /**
         * 端末内にあるVR動画を返す。VR動画であるかどうかは動画のアスペクト比が2:1または1:1であるかどうかによって判断する。
         */
        private fun getVRVideos(context: Context): Array<VideoInfo> {

            // 端末内の動画を取得
            val cursor: Cursor? = MediaStore.Video.query(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Video.VideoColumns.TITLE, // 0
                    MediaStore.Video.VideoColumns.DATA, // 1
                    MediaStore.Video.VideoColumns.DURATION, // 2
                    MediaStore.Video.VideoColumns.WIDTH, // 3
                    MediaStore.Video.VideoColumns.HEIGHT // 4
            ))

            // 結果を格納するList
            val videoList = mutableListOf<VideoInfo>()

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    val width = cursor.getInt(3)
                    val height = cursor.getInt(3)

                    // 2:1 または 1:1 の動画のみフィルタリング
                    if (width == height * 2 || width == height) {

                        val name = cursor.getString(0)
                        val path = File(cursor.getString(1)).relativeTo(Environment.getExternalStorageDirectory()).path
                        val length = cursor.getLong(2)

                        videoList.add(VideoInfo(
                                name,
                                path,
                                length)
                        )
                    }
                }
                cursor.close()
            }

            return videoList.toTypedArray()
        }
    }
}
