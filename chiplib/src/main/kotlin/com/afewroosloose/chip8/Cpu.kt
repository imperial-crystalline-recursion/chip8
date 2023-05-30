package com.afewroosloose.chip8

import com.afewroosloose.chip8.operation.*

class Cpu(private val memory: Memory, private val keyboard: Keyboard, private val display : Display) {
    fun interpretInstruction(operation: Int): Operation? {
        when (operation and 0xF000 shr 12) {
            0 -> {
                when {
                    (operation == 0x00E0) -> {
                        return ClearScreen()
                    }

                    (operation == 0x00EE) -> {
                        return Ret()
                    }

                    (operation and 0x0FFF > 0) -> { // syscall
                        println("We experienced a syscall?")
                    }
                }
            }

            1 -> {
                when {
                    (operation and 0x1000 != 0) -> { // jp
                        return Jump((operation xor 0x1000).toUShort())
                    }
                }
            }

            2 -> {
                val address = operation and 0xFFF
                return Call(address.toUShort())
            }

            3 -> {
                val register = (operation and 0xF00) shr 8
                val value = operation and 0xFF

                return SkipNextIfEqual(value.toUByte(), register)
            }

            4 -> {
                val register = (operation and 0xF00) shr 8
                val value = operation and 0xFF

                return SkipNextIfNotEqual(value.toUByte(), register)
            }

            5 -> {
                val x = operation and 0x0F00
                val y = operation and 0x00F0

                val shiftedX = x shr 8
                val shiftedY = y shr 4

                return SkipIfVxEqualsVy(shiftedX, shiftedY)
            }

            6 -> {
                val x = (operation and 0x0F00) shr 8
                val value = (operation and 0x00FF).toUByte()

                return StoreInVx(x, value)
            }

            7 -> {
                val x = (operation and 0x0F00) shr 8
                val value = (operation and 0x00FF).toUByte()

                return AddToVx(x, value)
            }

            8 -> {
                when (operation and 0xF) {
                    0 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return StoreVyInVx(x, y)
                    }

                    1 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return BitwiseOrVxAndVy(x, y)
                    }

                    2 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return BitwiseAndVxAndVy(x, y)
                    }

                    3 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return BitwiseXorVxAndVy(x, y)
                    }

                    4 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return AddVxAndVy(x, y)
                    }

                    5 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return SubtractVyFromVx(x, y)
                    }

                    6 -> {
                        val x = (operation and 0xF00) shr 8
                        return ShiftVxRight(x)
                    }

                    7 -> {
                        val x = (operation and 0xF00) shr 8
                        val y = (operation and 0xF0) shr 4
                        return SubtractVxFromVy(x, y)
                    }

                    0xE -> {
                        val x = (operation and 0xF00) shr 8
                        return ShiftVxLeft(x)
                    }
                }

            }

            9 -> {
                val x = (operation and 0xF00) shr 8
                val y = (operation and 0xF0) shr 4
                return SkipIfVxAndVyNotEqual(x, y)
            }

            0xA -> {
                val value = operation and 0xFFF
                return SetI(value.toUShort())
            }

            0xB -> {
                val address = (operation and 0xFFF).toUShort()
                return JumpOffsetV0(address)
            }

            0xC -> {
                val x = (operation and 0xF00) shr 8
                val kk = (operation and 0xFF).toUByte()
                return RandomIntoVx(x, kk)
            }

            0xD -> {
                println("instruction code is ${operation.toString(16)}")
                val x = operation shr 8 and 0x0F
                val y = operation shr 4 and 0x0F
                val n = operation and 0x0F

                memory.setDrawFlag()
                return Draw(x, y, n)

            }

            0xE -> {
                val x = operation shr 8 and 0x0F
                when (operation and 0xFF) {
                    0x9E -> {
                        return SkipIfKeyVxIsPressed(x, keyboard.getPressedKey())
                    }

                    0xA1 -> {
                        return SkipIfKeyVxIsNotPressed(x, keyboard.getPressedKey())
                    }
                }
            }

            0xF -> {
                val x = operation shr 8 and 0x0F
                when (operation and 0xFF) {
                    0x07 -> {
                        // set vx the timer delay value
                        return SetVxToDelayTimer(x)
                    }

                    0x0A -> {
                        // wait for keypress, store in Vx
                        return WaitForKeyPressAndStoreInVx(x, keyboard)
                    }

                    0x15 -> {
                        // set delay timer to Vx
                        return SetDelayTimerToVx(x)
                    }

                    0x18 -> {
                        return SetSoundTimerToVx(x)
                    }

                    0x1E -> {
                        return AddVxToI(x)
                    }
                    0x29 -> {
                        return GetLocationOfSpriteForVx(x)
                    }
                    0x33 -> {
                        return StoreBCDVxInMemoryPointedToByI(x)
                    }
                    0x55 -> {
                        return StoreV0ToVxInAddressPointedToByI(x)
                    }
                    0x65 -> {
                        return LoadV0ToVxFromAddressPointedToByI(x)
                    }
                }
            }
        }
        return null
    }

    fun execute() {
        while(true) {
            val currentTime = System.currentTimeMillis()
            val opcode = memory.getInstruction()
            val instruction = interpretInstruction(opcode.toInt())
            instruction?.execute(memory)
            if (memory.getAndClearDrawFlag()) {
                display.draw(memory.getScreenBuffer())
            }
            if (instruction !is JumpingOperation) {
                memory.setProgramCounter((memory.getProgramCounter() + 2u).toUShort())
            }
            if (memory.getDelayTimer() != 0u.toUByte()) {
                memory.setDelayTimer((memory.getDelayTimer()- 1u).toUByte())
            }
            Thread.sleep(1)
            val finalTime = System.currentTimeMillis()
            println("execution time was ${finalTime - currentTime}")
        }
    }
}