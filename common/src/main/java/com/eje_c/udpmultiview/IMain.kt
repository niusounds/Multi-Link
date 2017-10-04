package com.eje_c.udpmultiview

import com.eje_c.udpmultiview.data.ControlMessage

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
