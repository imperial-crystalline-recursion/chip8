package com.afewroosloose.chip8

class MockKeyboard: Keyboard {
    var key: Chip8Key = Chip8Key.NONE

    override fun getPressedKey(): Chip8Key {
        return key
    }

    override fun waitForKeyPress(): Chip8Key {
        Thread.sleep(1000)
        return key
    }
}