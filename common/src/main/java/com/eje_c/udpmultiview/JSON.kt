package com.eje_c.udpmultiview

import com.google.gson.Gson

/**
 * JSON文字列のシリアライズとデシリアライズを行う。
 */
object JSON {
    private val gson = Gson()

    /**
     * オブジェクトをJSON文字列へ変換する。
     */
    fun stringify(obj: Any): String = gson.toJson(obj)

    /**
     * JSON文字列からクラスのインスタンスを作成する。
     */
    fun <T> parse(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)
}
