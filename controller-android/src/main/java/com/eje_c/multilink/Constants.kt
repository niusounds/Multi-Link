package com.eje_c.multilink

/**
 * データクリア時、取得時などにどれだけ古いデータまで参照するか。単位はミリ秒。
 */
const val UPDATE_TIME_THRESHOLD_MILLIS = 10 * 60 * 60 * 1000