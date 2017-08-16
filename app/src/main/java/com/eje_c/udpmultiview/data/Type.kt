package com.eje_c.udpmultiview.data

/**
 * メッセージの種類を定義する整数値。
 */
enum class Type(val value: Int) {

    // data: none
    Ping(0),

    // data: ControlMessage
    Control(1),

    // data: boolean
    SendHeadTransform(2);

    companion object {
        fun fromInt(value: Int): Type {
            Type.values().forEach {
                if (it.value == value) return it
            }

            throw IllegalArgumentException("Unknown value $value.")
        }
    }
}
