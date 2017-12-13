package com.eje_c.udpmultiview

import android.os.Environment
import java.io.File

fun fromExternal(path: String): File = File(Environment.getExternalStorageDirectory(), path)