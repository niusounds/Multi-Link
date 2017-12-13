package com.eje_c.multilink

import org.json.JSONObject
import java.net.SocketAddress

/**
 * コントローラーからのメッセージを受信した際にEventBusを通じてこのオブジェクトが渡される。
 *
 * @param message コントローラーからのメッセージ内容。
 * @param remote コントローラーのアドレス情報
 */
data class ControlMessageReceiveEvent(val message: JSONObject, val remote: SocketAddress)
