package com.afewroosloose.chip8

import kotlin.experimental.and

class Memory {
    companion object {
        private const val MEMORY_SIZE = 0xFFF
        private const val MAX_ADDRESS = MEMORY_SIZE - 1
        private const val PROGRAM_START = 0x200
        private const val RESERVED_START = 0
        private const val RESERVED_END = 0x1FF

        private const val NUMBER_OF_REGISTERS = 16
        private const val SCREEN_START = 0
        private const val SCREEN_END = (64 * 32) / 8
    }

    private val memory = ByteArray(MEMORY_SIZE) { 0 }
    private val registers = ByteArray(NUMBER_OF_REGISTERS) { 0 }

    // various registers
    private var i: Short = 0 // used for storing memory adddresses
    private var pc: Short = 0 // program counter
    private var sp: Byte = 0  // stack pointer

    private var dt: Byte = 0 // delay timer
    private var st: Byte = 0 // sound timer

    private val stack2 = ArrayDeque<Short>()
    private val stack = Array<Short>(16) { 0 }

    fun load(byteArray: ByteArray) {
        if (byteArray.size > MEMORY_SIZE - PROGRAM_START) {
            throw InvalidProgramSizeException()
        }
        byteArray.forEachIndexed { index, byte ->
            memory[index + PROGRAM_START] = byte
        }
    }

    fun getV(x: Int): Byte {
        if (x in 0 until NUMBER_OF_REGISTERS) {
            return registers[x]
        } else {
            throw IllegalRegsiterException(x)
        }
    }

    fun setV(x: Int, value: Byte) {
        if (x in 0 until NUMBER_OF_REGISTERS) {
            registers[x] = value and 0xFF.toByte()
        } else {
            throw IllegalRegsiterException(x)
        }
    }

    fun setProgramCounter(address: Short) {
        pc = address
    }

    fun getStackPointer() = sp
    fun getProgramCounter(): Short = pc

    fun clearScreenBuffer() {
        // todo: clear screen buffer
    }

    fun getScreenBuffer(): Array<ByteArray> {
        return Array<ByteArray>(8) { ByteArray(4) { 0 } }
    }

    fun pushStack(value: Short) {
        if (sp >= 16) {
            throw StackOverflowException()
        } else {
            stack2.addLast(value)
            sp = (stack2.size - 1).toByte()
        }
    }

    fun popStack(): Short {
        val value = stack2.removeLast()
        sp = (stack2.size - 1).toByte()
        if (sp < 0) sp = 0
        return value
    }

    fun setI(value: Short) {
        i = value
    }

    fun getI() = i
}