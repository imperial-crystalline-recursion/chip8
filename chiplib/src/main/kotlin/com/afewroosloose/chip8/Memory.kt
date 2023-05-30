@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.afewroosloose.chip8

import kotlin.math.pow

class Memory() {
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

    private var draw: Boolean = false
    private val memory = UByteArray(MEMORY_SIZE) { 0.toUByte() }
    private val registers = UByteArray(NUMBER_OF_REGISTERS) { 0.toUByte() }

    // various registers
    private var i: UShort = 0.toUShort() // used for storing memory adddresses
    private var pc: UShort = 0x200.toUShort() // program counter
    private var sp: Byte = 0.toByte()  // stack pointer

    private var dt: UByte = 0.toUByte() // delay timer
    private var st: UByte = 0.toUByte() // sound timer

    private val stack2 = ArrayDeque<UShort>()
    private val stack = Array<UShort>(16) { 0.toUShort() }

    private var screenBuffer = Array<ULong>(32) { 0u }

    init {
        initInterpreter()
    }

    fun initInterpreter() {
        for (i in 0 until Font.ALPHABET.size) {
            memory[i] = Font.ALPHABET[i]
        }
    }

    fun load(byteArray: UByteArray) {
        resetState()

        initInterpreter()
        if (byteArray.size > MEMORY_SIZE - PROGRAM_START) {
            throw InvalidProgramSizeException()
        }
        byteArray.forEachIndexed { index, byte ->
            memory[index + PROGRAM_START] = byte
        }
    }

    private fun resetState() {
        for (i in memory.indices) {
            memory[i] = 0u
        }
        for (i in screenBuffer.indices) {
            screenBuffer[i] = 0u
        }
        for (i in registers.indices) {
            registers[i] = 0u
        }
        stack2.clear()
        dt = 0u
        st = 0u
        i = 0u
        pc = 0u
        sp = 0
        draw = false
    }

    fun getMemory() = memory.copyOf()

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
        for (i in 0 until screenBuffer.size) {
            screenBuffer[i] = 0u
        }
    }

    fun getScreenBuffer(): Array<ULong> {
        return screenBuffer.copyOf()
    }

    fun pushStack(value: UShort) {
        if (sp >= 16.toByte()) {
            throw StackOverflowException()
        } else {
            stack2.addLast(value)
            sp = (stack2.size - 1).toByte()
        }
    }

    fun popStack(): UShort {
        val value = stack2.removeLast()
        sp = (stack2.size - 1).toByte()
        if (sp < 0.toByte()) sp = 0.toByte()
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
    fun getSpriteStartLocation(digit: UByte): UByte {
        return memory[digit.toInt() * 5]
    }

    fun setMemory(i: Int, uByte: UByte) {
        memory[i] = uByte
    }

    fun getInstruction(): UShort {
        val addr = pc.toInt()
        val significantByte = memory[addr]
        val smallerByte = memory[addr + 1]

        val short: UShort = (significantByte * (2f.pow(8).toUInt()) + smallerByte).toUShort()

        return short
    }

    fun setScreenBuffer(screenBuffer: Array<ULong>) {
        this.screenBuffer = screenBuffer
    }

    fun setDrawFlag() {
        draw = true
    }

    fun getAndClearDrawFlag(): Boolean {
        val flag = draw
        draw = false
        return flag
    }
}