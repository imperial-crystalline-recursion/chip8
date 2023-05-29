@file:Suppress("DuplicatedCode")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.afewroosloose.chip8.operation

import com.afewroosloose.chip8.Keyboard
import com.afewroosloose.chip8.Memory
import com.afewroosloose.chip8.util.printAsSymbols
import com.afewroosloose.chip8.util.toBCD
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
        memory.setV(x, (xInt shr 1).toUByte ())
        memory.setV(0xF, leastSignificantBit.toUByte ())
    }
}

class ShiftVxLeft(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val xInt = memory.getV(x).toInt() and 0xFF
        val mostSignificantBit = xInt shr 7
        memory.setV(x, (xInt shl 1).toUByte ())
        memory.setV(0xF, mostSignificantBit.toUByte ())
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
        memory.setV(x, (Math.random() * 255).roundToInt().toUByte() and kk)
    }
}

class SkipIfKeyVxIsPressed(private val x: Int, private val pressedKeys: UShort): Operation {
    override fun execute(memory: Memory) {
        val keyToCheckFor = memory.getV(x).toUShort()
        val leftShift = keyToCheckFor
        val mask: UShort = 1u.rotateLeft(leftShift.toInt()).toUShort()
        if (pressedKeys and mask != 0u.toUShort()) {
            memory.setProgramCounter((memory.getProgramCounter() + 2.toUShort()).toUShort())
        }
    }
}

class SkipIfKeyVxIsNotPressed(private val x: Int, private val pressedKeys: UShort): Operation {
    override fun execute(memory: Memory) {
        val keyToCheckFor = memory.getV(x).toUShort()
        val leftShift = keyToCheckFor
        val mask: UShort = 1u.rotateLeft(leftShift.toInt()).toUShort()
        if (pressedKeys and mask == 0u.toUShort()) {
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
        val mask = 1u.toUShort()
        while(true) {
            val keys = keyboard.waitForKeyPress()
            for (i in 0 until UShort.SIZE_BITS) {
                if (keys.rotateRight(i) and mask == 1u.toUShort()) {
                    memory.setV(x, i.toUByte())
                    return;
                }
            }
        }
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

class GetLocationOfSpriteForVx(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        memory.getSpriteStartLocation(memory.getV(x))
    }
}

class StoreBCDVxInMemoryPointedToByI(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val vx = memory.getV(x)
        val bcd = vx.toBCD()

        val i = memory.getI().toInt()
        memory.setMemory(i, bcd[0])
        memory.setMemory(i + 1, bcd[1])
        memory.setMemory(i + 2, bcd[2])
    }
}

class StoreV0ToVxInAddressPointedToByI(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val i = memory.getI()
        for (index in 0..x) {
            val location = i.toInt() + index
            val value = memory.getV(index)
            memory.setMemory(location, value)
        }
        // note that this can't overflow in practice because memory is always from 0x0 to 0xFFF, but I put this just in case.
        val vF = if (i.toUInt() + x.toUShort() + 1u > 0xFFFFu) 1 else 0
        val newI = (i + x.toUShort() + 1u)
        memory.setI(newI.toUShort())
        memory.setV(0xF, vF.toUByte())
    }
}

class LoadV0ToVxFromAddressPointedToByI(private val x: Int): Operation {
    override fun execute(memory: Memory) {
        val i = memory.getI()
        for (index in 0..x) {
            val location = i.toInt() + index
            val value = memory.getMemory()[location]
            memory.setV(index, value)
        }
        // note that this can't overflow in practice because memory is always from 0x0 to 0xFFF, but I put this just in case.
        val vF = if (i.toUInt() + x.toUShort() + 1u > 0xFFFFu) 1 else 0
        val newI = (i + x.toUShort() + 1u)
        memory.setI(newI.toUShort())
        memory.setV(0xF, vF.toUByte())
    }
}

/**
 * Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels. Each row of 8 pixels is
 * read as bit-coded starting from memory location I; I value does not change after the execution of this instruction.
 * As described above, VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, and
 * to 0 if that does not happen.
 */
class Draw(private val x: Int, private val y: Int, private val n: Int): Operation {
    override fun execute(memory: Memory) {
        val i = memory.getI().toInt()
        val sprite = Array<UByte>(n) { 0u }

        val vy = memory.getV(y).toInt()
        val vx = memory.getV(x).toInt()

        val memoryArray = memory.getMemory()

        // load index
        for (index in 0 until n) {
            sprite[index] = memoryArray[index + i]
        }

        val screenBuffer = memory.getScreenBuffer()

        var collision = false
        sprite.forEachIndexed { idx, row ->
            val rowAsUInt = row.toULong().rotateRight(vx + 8)
            val currentRowWrapped = (idx + vy) % 32
            val currentRowSum = screenBuffer[currentRowWrapped] + rowAsUInt
            val currentRowXor = screenBuffer[currentRowWrapped] xor rowAsUInt
            if (currentRowSum != currentRowXor) {
                collision = true
            }
            screenBuffer[currentRowWrapped] = currentRowXor
        }
        memory.setV(0xF, if (collision) 1 else 0)
        screenBuffer.forEach {
            it.printAsSymbols()
        }
        memory.setScreenBuffer(screenBuffer)
    }
}