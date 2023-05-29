package com.afewroosloose.chip8driver.io

import com.afewroosloose.chip8.Keyboard
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class Chip8Keyboard: Keyboard, KeyListener {
    private var pressedKeys: UShort = 0u
    private var heldKeys: UShort = 0u
    private var releasedKeys: UShort = 0u

    override fun getPressedKey(): UShort {
        return pressedKeys
    }

    override fun waitForKeyPress(): UShort {
        while (pressedKeys == 0u.toUShort()) {
            Thread.sleep(10)
        }
        return pressedKeys
    }

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent?) {
        println("pressed key ${e?.keyChar}")
        val index = keys.indexOf(e?.keyChar?.lowercaseChar())
        pressedKeys = pressedKeys xor 0x01.toUShort().rotateLeft(index)
    }

    override fun keyReleased(e: KeyEvent?) {
        println("released key ${e?.keyChar}")
        val index = keys.indexOf(e?.keyChar?.lowercaseChar())
        pressedKeys = pressedKeys xor 0x01.toUShort().rotateLeft(index)
    }

    companion object {
        val keys = arrayOf('0', '1', '2', '3' ,'4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    }
}