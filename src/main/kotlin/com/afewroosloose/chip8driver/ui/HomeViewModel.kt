package com.afewroosloose.chip8driver.ui

import com.afewroosloose.chip8.Cpu
import com.afewroosloose.chip8.Display
import com.afewroosloose.chip8.Keyboard
import com.afewroosloose.chip8.Memory
import com.afewroosloose.chip8driver.io.Chip8Keyboard

class HomeViewModel(private val display: Display) {
    private val memory = Memory()
    internal val keyboard = Chip8Keyboard()

    private val cpu = Cpu(memory, keyboard, display)

    fun loadRom(fileBytes: ByteArray) {
        display.draw(Array<ULong>(32) { 0u })
        memory.load(fileBytes.toUByteArray())
        cpu.execute()
    }
}