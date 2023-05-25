@file:Suppress("DuplicatedCode")

package com.afewroosloose.chip8.operation

import com.afewroosloose.chip8.Chip8Key
import com.afewroosloose.chip8.Cpu
import com.afewroosloose.chip8.Keyboard
import com.afewroosloose.chip8.Memory
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor
import kotlin.math.roundToInt

interface Operation {
    fun execute(memory: Memory)
}

interface JumpingOperation

class Jump(private val address: UShort) : Operation, JumpingOperation {
    override fun execute(memory: Memory) {
        memory.setProgramCounter(address)
    }
}

class Sys(private val address: UShort) : Operation {
    override fun execute(memory: Memory) {
        TODO("Not yet implemented")
    }
}

class ClearScreen() : Operation {
    override fun execute(memory: Memory) {
        memory.clearScreenBuffer()
    }
}

class Ret : Operation {
    override fun execute(memory: Memory) {
        val newProgramCounter = memory.popStack()
        memory.setProgramCounter(newProgramCounter)
    }
}

class Call(private val address: UShort) : Operation, JumpingOperation {
    override fun execute(memory: Memory) {
        memory.pushStack(memory.getProgramCounter())
        memory.setProgramCounter(address)
    }
}

class SkipNextIfEqual(private val value: UByte , private val register: Int) : Operation {
    override fun execute(memory: Memory) {
        val registerValue = memory.getV(register)
        if (registerValue == value) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class SkipNextIfNotEqual(private val value: UByte , private val register: Int) : Operation {
    override fun execute(memory: Memory) {
        val registerValue = memory.getV(register)
        if (registerValue != value) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class SkipIfVxEqualsVy(private val x: Int, private val y: Int) : Operation {
    override fun execute(memory: Memory) {
        if (memory.getV(x) == memory.getV(y)) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class StoreInVx(private val x: Int, private val value: UByte ): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, value)
    }
}

class AddToVx(private val x: Int, private val value: UByte ): Operation {
    override fun execute(memory: Memory) {
        val newVx = (memory.getV(x) + value) and 0x00FF.toUInt()
        memory.setV(x, newVx.toUByte ())
    }
}

class StoreVyInVx(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, memory.getV(y))
    }
}

class BitwiseOrVxAndVy(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, memory.getV(x) or memory.getV(y))
    }
}

class BitwiseAndVxAndVy(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, memory.getV(x) and memory.getV(y))
    }
}

class BitwiseXorVxAndVy(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, memory.getV(x) xor memory.getV(y))
    }
}

class AddVxAndVy(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        val xInt = memory.getV(x).toInt() and 0xFF
        val yInt = memory.getV(y).toInt() and 0xFF
        val sum = xInt + yInt
        val truncatedSum = sum and 0xFF
        val carry = sum and 0x100 shr 8
        memory.setV(x, truncatedSum.toUByte ())
        memory.setV(0xF, carry.toUByte ())
    }
}

class SubtractVyFromVx(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        val xInt = memory.getV(x).toInt() and 0xFF
        val yInt = memory.getV(y).toInt() and 0xFF
        val noBorrow = if (xInt > yInt) 1 else 0
        val sum = xInt - yInt
        memory.setV(x, sum.toUByte ())
        memory.setV(0xF, noBorrow.toUByte ())
    }
}

class SubtractVxFromVy(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        val xInt = memory.getV(x).toInt() and 0xFF
        val yInt = memory.getV(y).toInt() and 0xFF
        val noBorrow = if (yInt > xInt) 1 else 0
        val sum = yInt - xInt
        memory.setV(x, sum.toUByte ())
        memory.setV(0xF, noBorrow.toUByte ())
    }
}

class ShiftVxRight(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val xInt = memory.getV(x).toInt() and 0xFF
        val leastSignificantBit = if (xInt and 0x1 != 0) 1 else 0
        memory.setV(0xF, leastSignificantBit.toUByte ())
        memory.setV(x, (xInt shr 1).toUByte ())
    }
}

class ShiftVxLeft(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val xInt = memory.getV(x).toInt() and 0xFF
        val mostSignificantBit = xInt shr 7
        memory.setV(0xF, mostSignificantBit.toUByte ())
        memory.setV(x, (xInt shl 1).toUByte ())
    }
}

class SkipIfVxAndVyNotEqual(private val x: Int, private val y: Int): Operation {
    override fun execute(memory: Memory) {
        if (memory.getV(x) != memory.getV(y)) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class SetI(private val value: UShort): Operation {
    override fun execute(memory: Memory) {
        memory.setI(value)
    }
}

class JumpOffsetV0(private val value: UShort): Operation, JumpingOperation {
    override fun execute(memory: Memory) {
        memory.setProgramCounter((memory.getV(0).toUShort() + value).toUShort())
    }
}

class RandomIntoVx(private val x: Int, private val kk: UByte ): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, (Math.random() * 255).roundToInt().toUByte () and kk)
    }
}

class Draw(private val n: UByte , private val x: UByte , private val y: UByte ) {

}

class SkipIfKeyVxIsPressed(private val x: Int, private val pressedKey: Chip8Key): Operation {
    override fun execute(memory: Memory) {
        if (memory.getV(x).toInt() == pressedKey.ordinal) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class SkipIfKeyVxIsNotPressed(private val x: Int, private val pressedKey: Chip8Key): Operation {
    override fun execute(memory: Memory) {
        if (memory.getV(x).toInt() != pressedKey.ordinal) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class SetVxToDelayTimer(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, memory.getDelayTimer())
    }
}

class WaitForKeyPressAndStoreInVx(private val x: Int, private val keyboard: Keyboard): Operation {
    override fun execute(memory: Memory) {
        memory.setV(x, keyboard.waitForKeyPress().byte )
    }
}

class SetDelayTimerToVx(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setDelayTimer(memory.getV(x))
    }
}

class SetSoundTimerToVx(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        memory.setSoundTimer(memory.getV(x))
    }
}

class AddVxToI(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val i = memory.getI().toUInt()
        val vx = memory.getV(x)
        val result = i + vx
        val overFlow = result.toInt() > 0xFFFF
        memory.setV(0xF, (if (overFlow) 1 else 0).toUByte())
        memory.setI(result.toUShort())
    }
}