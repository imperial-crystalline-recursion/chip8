package com.afewroosloose.chip8driver.io

import com.afewroosloose.chip8.Keyboard
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class Chip8Keyboard: Keyboard, KeyListener {
    private var pressedKeys: UShort = 0u
    private var heldKeys: UShort = 0u
    private var releasedKeys: UShort = 0u
    private var waiting = false

    override fun getPressedKey(): UShort {
        return pressedKeys
    }

    override fun waitForKeyPress(): UShort {
        waiting = true
        while (releasedKeys == 0u.toUShort()) {
            Thread.sleep(10)
        }
        val value = releasedKeys
        releasedKeys = 0u
        return value
    }

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent?) {
        println("pressed key ${e?.keyChar}")
        val index = keys.indexOf(e?.keyChar?.lowercaseChar())
        val mask =  0x01.toUShort().rotateLeft(index)
        if (pressedKeys and mask == 0u.toUShort()) {
            pressedKeys = pressedKeys xor mask
        }
        println("Pressed key array is ${pressedKeys.toString(2).padEnd(16, '0')}")
    }

    override fun keyReleased(e: KeyEvent?) {
        println("released key ${e?.keyChar}")
        val index = keys.indexOf(e?.keyChar?.lowercaseChar())
        val mask =  0x01.toUShort().rotateLeft(index)
        if (pressedKeys and mask != 0u.toUShort()) {
            pressedKeys = pressedKeys xor mask
        }
        if (waiting) {
            releasedKeys = mask
        }
        println("Pressed key array is ${pressedKeys.toString(2).padEnd(16, '0')}")
    }

    companion object {
        val keys = arrayOf('0', '1', '2', '3' ,'4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    }
}