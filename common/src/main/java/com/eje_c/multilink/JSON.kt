package com.eje_c.multilink

import com.google.gson.Gson

/**
 * JSON文字列のシリアライズとデシリアライズを行う。
 */
object JSON {
    val gson = Gson()

    /**
     * オブジェクトをJSON文字列へ変換する。
     */
    fun stringify(obj: Any): String = gson.toJson(obj)

    /**
     * JSON文字列からクラスのインスタンスを作成する。
     */
    inline fun <reified T> parse(json: String): T = gson.fromJson(json, T::class.java)
}
