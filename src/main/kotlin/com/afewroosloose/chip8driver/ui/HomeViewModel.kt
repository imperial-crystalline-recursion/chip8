package com.afewroosloose.chip8driver.ui

import com.afewroosloose.chip8.Cpu
import com.afewroosloose.chip8.Display
import com.afewroosloose.chip8.Keyboard
import com.afewroosloose.chip8.Memory
import com.afewroosloose.chip8driver.io.Chip8Keyboard

class HomeViewModel(display: Display) {
    private val memory = Memory()
    internal val keyboard = Chip8Keyboard()

    private val cpu = Cpu(memory, keyboard, display)

    fun loadRom(fileBytes: ByteArray) {
        memory.load(fileBytes.toUByteArray())
        Thread(object: Runnable {
            override fun run() {
                cpu.execute()
            }
        }).start()
    }
}