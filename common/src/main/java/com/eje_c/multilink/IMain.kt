package com.eje_c.multilink

import com.eje_c.multilink.data.ControlMessage

interface IMain {

    /**
     * GLスレッドで処理を実行する。
     */
    fun runOnGlThread(command: () -> Unit)

    /**
     * コントローラーからのコントロールメッセージに応答する。
     */
    fun updateState(controlMessage: ControlMessage)
}
