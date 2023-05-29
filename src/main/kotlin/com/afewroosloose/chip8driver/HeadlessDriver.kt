package com.afewroosloose.chip8driver

import com.afewroosloose.chip8.Cpu
import com.afewroosloose.chip8.Display
import com.afewroosloose.chip8.Keyboard
import com.afewroosloose.chip8.Memory


        fun main() {
            val memory = Memory()
            val keyboard = object : Keyboard {
                override fun getPressedKey(): UShort {
                    return 0u
                }

                override fun waitForKeyPress(): UShort {
                    return 0u
                }

            }
            val cpu = Cpu(memory, keyboard, object : Display {
                override fun draw(screenBuffer: Array<ULong>) {

                }

            })

            val rom = memory::class.java.classLoader.getResourceAsStream("flags.ch8").readBytes().toUByteArray()

            memory.load(rom)
            cpu.execute()
        }