package com.afewroosloose.chip8

interface Display {
    fun draw(screenBuffer: Array<ULong>)
}