package com.afewroosloose.chip8driver

import com.afewroosloose.chip8.Chip8Key
import com.afewroosloose.chip8.Cpu
import com.afewroosloose.chip8.Keyboard
import com.afewroosloose.chip8.Memory


        fun main() {
            val memory = Memory()
            val keyboard = object : Keyboard {
                override fun getPressedKey(): Chip8Key {
                    return Chip8Key.NONE
                }

                override fun waitForKeyPress(): Chip8Key {
                    return Chip8Key.NONE
                }

            }
            val cpu = Cpu(memory, keyboard)

            val rom = memory::class.java.classLoader.getResourceAsStream("flags.ch8").readBytes().toUByteArray()

            memory.load(rom)
            cpu.execute()
        }