package cn.edu.nju.cs

fun Any.i() = this as Int

/**
 * Returns Mini Java's char
 */
fun Any.c() = (this as Int).toByte().toInt().toChar()

/**
 * Returns `A` in the given string in the format of `A::B`
 */
fun String.getType() = this.split("::").getOrNull(0)