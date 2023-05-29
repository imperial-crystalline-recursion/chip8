package com.afewroosloose.chip8

class MockKeyboard: Keyboard {
    var key: UShort = 0u

    override fun getPressedKey(): UShort {
        return key
    }

    override fun waitForKeyPress(): UShort {
        Thread.sleep(1000)
        return key
    }
}