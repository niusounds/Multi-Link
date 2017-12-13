package com.eje_c.multilink

/**
 * How much old data is retrieved when querying. Unit is milliseconds.
 */
const val UPDATE_TIME_THRESHOLD_MILLIS = 10 * 60 * 60 * 1000

/**
 * How much old data is deleted when clearing. Unit is milliseconds.
 */
const val UPDATE_TIME_THRESHOLD_FOR_CLEAR = 60 * 60 * 1000
