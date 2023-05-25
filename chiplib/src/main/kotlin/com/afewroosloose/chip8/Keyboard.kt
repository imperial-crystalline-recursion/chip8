package com.afewroosloose.chip8

interface Keyboard {
    fun getPressedKey(): Chip8Key
    fun waitForKeyPress(): Chip8Key
}

enum class Chip8Key {
    ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, A, B, C, D, E, F, NONE;

    val byte: Byte = ordinal.toByte()
}