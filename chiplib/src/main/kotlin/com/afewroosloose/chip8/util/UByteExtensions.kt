@file:OptIn(ExperimentalUnsignedTypes::class)

package com.afewroosloose.chip8.util

fun UByte.toBCD(): UByteArray {
    val hundreds = (this / 100.toUInt()).toUByte()
    val tens = (this % 100.toUInt() / 10.toUInt()).toUByte()
    val ones = (this % 10.toUInt()).toUByte()
    return ubyteArrayOf(hundreds, tens, ones)
}

fun ULong.printAsSymbols() {
    var str = ""
    var mask: ULong = 0x01.toULong().rotateRight(1)
    for (i in 0 until ULong.SIZE_BITS) {
        str += if (this and mask > 1.toULong()) "#" else "."
        mask /= 2u
    }
    println(str)
}