package com.afewroosloose.chip8

interface Keyboard {
    /**
     * Returns a UShort. Each bit corresponds to a key. Smallest is 0, largest is F
     */
    fun getPressedKey(): UShort

    /**
     * Returns a UShort. Each bit corresponds to a key. Smallest is 0, largest is F
     */
    fun waitForKeyPress(): UShort
}