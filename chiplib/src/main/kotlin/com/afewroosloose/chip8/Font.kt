@file:OptIn(ExperimentalUnsignedTypes::class)

package com.afewroosloose.chip8

class Font {
    companion object {
        val ZERO = ubyteArrayOf(
            0xF0,
            0x90,
            0x90,
            0x90,
            0xF0
        )
        val ONE = ubyteArrayOf(
            0x20,
            0x60,
            0x20,
            0x20,
            0x70
        )
        val TWO = ubyteArrayOf(
            0xF0,
            0x10,
            0xF0,
            0x80,
            0xF0
        )
        val THREE = ubyteArrayOf(
            0xF0,
            0x10,
            0xF0,
            0x10,
            0xF0
        )
        val FOUR = ubyteArrayOf(
            0x90,
            0x90,
            0xF0,
            0x10,
            0x10
        )
        val FIVE = ubyteArrayOf(
            0xF0,
            0x80,
            0xF0,
            0x10,
            0xF0
        )
        val SIX = ubyteArrayOf(
            0xF0,
            0x80,
            0xF0,
            0x90,
            0xF0
        )
        val SEVEN = ubyteArrayOf(
            0xF0,
            0x10,
            0x20,
            0x40,
            0x40
        )
        val EIGHT = ubyteArrayOf(
            0xF0,
            0x90,
            0xF0,
            0x90,
            0xF0
        )
        val NINE = ubyteArrayOf(
            0xF0,
            0x90,
            0xF0,
            0x10,
            0xF0
        )
        val A = ubyteArrayOf(
            0xF0,
            0x90,
            0xF0,
            0x90,
            0x90
        )
        val B = ubyteArrayOf(
            0xE0,
            0x90,
            0xE0,
            0x90,
            0xE0
        )

        val C = ubyteArrayOf(
            0xF0,
            0x80,
            0x80,
            0x80,
            0xF0
        )

        val D = ubyteArrayOf(
            0xE0,
            0x90,
            0x90,
            0x90,
            0xE0
        )

        val E = ubyteArrayOf(
            0xF0,
            0x80,
            0xF0,
            0x80,
            0xF0
        )

        val F = ubyteArrayOf(
            0xF0,
            0x80,
            0xF0,
            0x80,
            0x80
        )

        val ALPHABET: UByteArray = ubyteArrayOf()
            .plus(ZERO)
            .plus(ONE)
            .plus(TWO)
            .plus(THREE)
            .plus(FOUR)
            .plus(FIVE)
            .plus(SIX)
            .plus(SEVEN)
            .plus(EIGHT)
            .plus(NINE)
            .plus(A)
            .plus(B)
            .plus(C)
            .plus(D)
            .plus(E)
            .plus(F)
    }
}

fun ubyteArrayOf(vararg integers: Int): UByteArray {
    return integers.map {
        (it and 0xFF).toUByte()
    }.toUByteArray()
}

fun UByteArray.plusAll(arrays: List<UByteArray>): UByteArray {
    val array = ubyteArrayOf()
    arrays.forEach {
        array.plus(it)
    }
    return array
}