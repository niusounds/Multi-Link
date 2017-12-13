package com.eje_c.udpmultiview.data

/**
 * コントローラーに送信する端末情報を表すクラス。
 */
class DeviceInfo(val imei: String, val name: String, val videos: List<VideoInfo>)
