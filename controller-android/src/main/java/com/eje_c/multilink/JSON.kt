package com.eje_c.multilink

import com.google.gson.Gson

object JSON {
    val gson = Gson()

    /**
     * Convert an object to a JSON string.
     */
    fun stringify(obj: Any): String = gson.toJson(obj)

    /**
     * Construct an object from JSON string.
     */
    inline fun <reified T> parse(json: String): T = gson.fromJson(json, T::class.java)
}