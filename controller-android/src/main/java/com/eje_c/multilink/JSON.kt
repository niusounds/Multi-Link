package com.eje_c.multilink

import com.google.gson.Gson

object JSON {
    val gson = Gson()

    fun stringify(obj: Any): String = gson.toJson(obj)

    inline fun <reified T> parse(json: String): T = gson.fromJson(json, T::class.java)
}