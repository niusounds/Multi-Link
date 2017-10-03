package com.eje_c.udpmultiview.cardboard

import android.media.MediaPlayer

import org.rajawali3d.cameras.Camera
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.StreamingTexture
import org.rajawali3d.math.Matrix4
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Sphere

/**
 * Inverted UV sphere for rendering 360 video.
 */
class VRSphere : Sphere(100f, 128, 64) {

    private var streamingTexture: StreamingTexture? = null

    init {
        setRotation(Vector3.Axis.Y, -90.0)
        setScale(1.0, 1.0, -1.0)
        setColor(0)
        material = Material()
    }

    @Throws(ATexture.TextureException::class)
    fun bindMediaPlayer(player: MediaPlayer) {

        if (streamingTexture != null) {
            streamingTexture!!.updateMediaPlayer(player)
        } else {
            streamingTexture = StreamingTexture("video", player)
            material.addTexture(streamingTexture)
        }

    }

    override fun render(camera: Camera, vpMatrix: Matrix4, projMatrix: Matrix4, vMatrix: Matrix4, parentMatrix: Matrix4?, sceneMaterial: Material?) {
        streamingTexture?.update()
        super.render(camera, vpMatrix, projMatrix, vMatrix, parentMatrix, sceneMaterial)
    }
}
