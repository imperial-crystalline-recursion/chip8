@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.afewroosloose.chip8

import java.util.UUID
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

    private val memory = UByteArray(MEMORY_SIZE) { 0.toUByte() }
    private val registers = UByteArray(NUMBER_OF_REGISTERS) { 0.toUByte() }

    // various registers
    private var i: UShort = 0.toUShort() // used for storing memory adddresses
    private var pc: UShort = 0.toUShort() // program counter
    private var sp: UByte = 0.toUByte()  // stack pointer

    private var dt: UByte = 0.toUByte() // delay timer
    private var st: UByte = 0.toUByte() // sound timer

    private val stack2 = ArrayDeque<UShort>()
    private val stack = Array<UShort>(16) { 0.toUShort() }

    fun initInterpreter() {
        for (i in 0 until Font.ALPHABET.size) {
            memory[i] = Font.ALPHABET[i]
        }
    }

    fun load(byteArray: UByteArray) {
        if (byteArray.size > MEMORY_SIZE - PROGRAM_START) {
            throw InvalidProgramSizeException()
        }
        byteArray.forEachIndexed { index, byte ->
            memory[index + PROGRAM_START] = byte
        }
    }

    fun getV(x: Int): UByte {
        if (x in 0 until NUMBER_OF_REGISTERS) {
            return registers[x]
        } else {
            throw IllegalRegsiterException(x)
        }
    }

    fun setV(x: Int, value: Byte) {
        setV(x, (value.toInt() and 0xFF).toUByte())
    }

    fun setV(x: Int, value: UByte) {
        if (x in 0 until NUMBER_OF_REGISTERS) {
            registers[x] = value and 0xFF.toUByte()
        } else {
            throw IllegalRegsiterException(x)
        }
    }

    fun setProgramCounter(address: UShort) {
        pc = address
    }

    fun getStackPointer() = sp
    fun getProgramCounter(): UShort = pc

    fun clearScreenBuffer() {
        // todo: clear screen buffer
    }

    fun getScreenBuffer(): Array<UByteArray> {
        return Array<UByteArray>(8) { UByteArray(4) { 0.toUByte() } }
    }

    fun pushStack(value: UShort) {
        if (sp >= 16.toUByte()) {
            throw StackOverflowException()
        } else {
            stack2.addLast(value)
            sp = (stack2.size - 1).toUByte()
        }
    }

    fun popStack(): UShort {
        val value = stack2.removeLast()
        sp = (stack2.size - 1).toUByte()
        if (sp < 0.toUByte()) sp = 0.toUByte()
        return value
    }

    fun setI(value: UShort) {
        i = value
    }

    fun getI(): UShort = i
    fun getDelayTimer() = dt

    fun setDelayTimer(value: UByte) {
        dt = value
    }

    fun setSoundTimer(value: UByte) {
        st = value
    }

    fun getSoundTimer() = st
}