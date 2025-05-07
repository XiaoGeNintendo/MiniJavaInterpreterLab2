package cn.edu.nju.cs

fun Any.i() = this as Int

/**
 * Returns Mini Java's char
 */
fun Any.c() = (this as Int).toByte().toInt().toChar()

fun String.getType() = this.split("::").getOrNull(0)