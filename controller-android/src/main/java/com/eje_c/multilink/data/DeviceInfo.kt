package com.eje_c.multilink.data

/**
 * VR device information.
 */
class DeviceInfo(val imei: String, val name: String, val videos: List<VideoInfo>) {

    /**
     * Video metadata in VR device.
     */
    class VideoInfo(val name: String, val path: String, val length: Long)
}
