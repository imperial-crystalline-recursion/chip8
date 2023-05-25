package com.afewroosloose.chip8

import com.afewroosloose.chip8.operation.*
import com.afewroosloose.chip8.utils.assertAllZero
import com.afewroosloose.chip8.utils.assertType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class CpuTests {

    lateinit var cpu: Cpu
    lateinit var memory: Memory
    lateinit var keyboard: MockKeyboard

    @Before
    fun setup() {
        memory = Memory()
        keyboard = MockKeyboard()
        cpu = Cpu(memory, keyboard)
    }

    @Test
    fun `test jp operation`() {
        val operation = cpu.interpretInstruction(0x1333)
        operation.assertType<Jump>()

        operation!!.execute(memory)
        assertEquals("The program counter is at 0x333", memory.getProgramCounter(), 0x333.toUShort())
    }

    @Test
    fun `test cls operation`() {
        val operation = cpu.interpretInstruction(0x00E0)

        operation.assertType<ClearScreen>()

        operation!!.execute(memory)
        memory.getScreenBuffer().assertAllZero()
    }

    @Test
    fun `test ret operation`() {
        memory.pushStack(1.toUShort())
        memory.pushStack(2.toUShort())
        memory.pushStack(3.toUShort())

        assertEquals(2.toByte(), memory.getStackPointer()) // we are pointing at frame 2
        val operation = cpu.interpretInstruction(0x00EE)
        operation.assertType<Ret>()

        operation!!.execute(memory)
        assertEquals("We should now have 3 on the program counter, since that was the last thing added to the stack", 3.toUShort(), memory.getProgramCounter())
        assertEquals("Stack pointer should now point at 1", 1.toByte(), memory.getStackPointer())
    }

    @Test
    fun `test call operation`() {
        val operation = cpu.interpretInstruction(0x2987)
        operation.assertType<Call>()

        memory.setProgramCounter(5.toUShort())
        operation!!.execute(memory)
        assertEquals(5.toUShort(), memory.popStack())
        assertEquals(0x987.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if equals when true`() {
        memory.setV(1, 50)
        val operation = cpu.interpretInstruction(0x3132)

        operation.assertType<SkipNextIfEqual>()

        operation!!.execute(memory)
        assertEquals(2.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if equals when not true`() {
        memory.setV(1, 50)
        val operation = cpu.interpretInstruction(0x3131)

        operation.assertType<SkipNextIfEqual>()

        operation!!.execute(memory)
        assertEquals(0.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if not equals when true`() {
        memory.setV(1, 50)
        val operation = cpu.interpretInstruction(0x4132)

        operation.assertType<SkipNextIfNotEqual>()

        operation!!.execute(memory)
        assertEquals(0.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if not equals when not true`() {
        memory.setV(1, 50)
        val operation = cpu.interpretInstruction(0x4131)

        operation.assertType<SkipNextIfNotEqual>()

        operation!!.execute(memory)
        assertEquals(2.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if Vx == Vy is true`() {
        memory.setV(1, 5)
        memory.setV(2, 5)

        val operation = cpu.interpretInstruction(0x5120)
        operation.assertType<SkipIfVxEqualsVy>()

        operation!!.execute(memory)
        assertEquals(2.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test not skip if Vx == Vy is false`() {
        memory.setV(1, 4)
        memory.setV(2, 5)

        val operation = cpu.interpretInstruction(0x5120)
        operation.assertType<SkipIfVxEqualsVy>()

        operation!!.execute(memory)
        assertEquals(0.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test storing byte in V register`() {
        val operation = cpu.interpretInstruction(0x6133)
        operation.assertType<StoreInVx>()

        operation!!.execute(memory)
        val actual: Byte = memory.getV(1)
        assertEquals(0x33.toByte(), actual)
    }

    @Test
    fun `test add to V register`() {
        memory.setV(1, 1)
        val operation = cpu.interpretInstruction(0x7133)
        operation.assertType<AddToVx>()

        operation!!.execute(memory)
        val actual: Byte = memory.getV(1)
        assertEquals(0x34.toByte(), actual)
    }

    @Test
    fun `test add to V register with overflow`() {
        memory.setV(1, 0xFF.toByte())
        val operation = cpu.interpretInstruction(0x7101)
        operation.assertType<AddToVx>()

        operation!!.execute(memory)
        val actual: Byte = memory.getV(1)
        assertEquals(0.toByte(), actual)
    }

    @Test
    fun `test put vy in vx`() {
        memory.setV(1, 0)
        memory.setV(2, 5)

        val operation = cpu.interpretInstruction(0x8120)
        operation.assertType<StoreVyInVx>()

        operation!!.execute(memory)

        assertEquals(5.toByte(), memory.getV(1))
        assertEquals(5.toByte(), memory.getV(2))
    }

    @Test
    fun `test bitwise or on vx and vy`() {
        memory.setV(1, 0b101)
        memory.setV(2, 0b110)

        val operation = cpu.interpretInstruction(0x8121)
        operation.assertType<BitwiseOrVxAndVy>()

        operation!!.execute(memory)

        assertEquals(0b111.toByte(), memory.getV(1))
        assertEquals(0b110.toByte(), memory.getV(2))
    }

    @Test
    fun `test bitwise and on vx and vy`() {
        memory.setV(1, 0b101)
        memory.setV(2, 0b110)

        val operation = cpu.interpretInstruction(0x8122)
        operation.assertType<BitwiseAndVxAndVy>()

        operation!!.execute(memory)

        assertEquals(0b100.toByte(), memory.getV(1))
        assertEquals(0b110.toByte(), memory.getV(2))
    }

    @Test
    fun `test bitwise xor on vx and vy`() {
        memory.setV(1, 0b101)
        memory.setV(2, 0b110)

        val operation = cpu.interpretInstruction(0x8123)
        operation.assertType<BitwiseXorVxAndVy>()

        operation!!.execute(memory)

        assertEquals(0b011.toByte(), memory.getV(1))
        assertEquals(0b110.toByte(), memory.getV(2))
    }

    @Test
    fun `test bitwise plus on vx and vy no carry`() {
        memory.setV(1, 3)
        memory.setV(2, 4)

        val operation = cpu.interpretInstruction(0x8124)
        operation.assertType<AddVxAndVy>()

        operation!!.execute(memory)

        assertEquals(7.toByte(), memory.getV(1))
        assertEquals(4.toByte(), memory.getV(2))
        assertEquals(0.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test bitwise plus on vx and vy with carry`() {
        memory.setV(1, 0xFF.toByte())
        memory.setV(2, 1)

        val operation = cpu.interpretInstruction(0x8124)
        operation.assertType<AddVxAndVy>()

        operation!!.execute(memory)

        assertEquals(0.toByte(), memory.getV(1))
        assertEquals(1.toByte(), memory.getV(2))
        assertEquals(1.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test subtraction on vx and vy with no carry`() {
        memory.setV(1, 3)
        memory.setV(2, 1)

        val operation = cpu.interpretInstruction(0x8125)
        operation.assertType<SubtractVyFromVx>()

        operation!!.execute(memory)

        assertEquals(2.toByte(), memory.getV(1))
        assertEquals(1.toByte(), memory.getV(2))
        assertEquals(1.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test subtraction on vx and vy with carry`() {
        memory.setV(1, 3)
        memory.setV(2, 4)

        val operation = cpu.interpretInstruction(0x8125)
        operation.assertType<SubtractVyFromVx>()

        operation!!.execute(memory)

        assertEquals((-1).toByte(), memory.getV(1))
        assertEquals(4.toByte(), memory.getV(2))
        assertEquals(0.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test shr vx with odd number`() {
        memory.setV(1, 0b111)
        val operation = cpu.interpretInstruction(0x8106)
        operation.assertType<ShiftVxRight>()

        operation!!.execute(memory)

        assertEquals(0b11.toByte(), memory.getV(1))
        assertEquals(1.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test shr vx with even number`() {
        memory.setV(1, 0b110)
        val operation = cpu.interpretInstruction(0x8106)
        operation.assertType<ShiftVxRight>()

        operation!!.execute(memory)

        assertEquals(0b11.toByte(), memory.getV(1))
        assertEquals(0.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test subtraction on vy and vx with no carry`() {
        memory.setV(1, 1)
        memory.setV(2, 3)

        val operation = cpu.interpretInstruction(0x8127)
        operation.assertType<SubtractVxFromVy>()

        operation!!.execute(memory)

        assertEquals(2.toByte(), memory.getV(1))
        assertEquals(3.toByte(), memory.getV(2))
        assertEquals(1.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test subtraction on vy and vx with carry`() {
        memory.setV(1, 4)
        memory.setV(2, 3)

        val operation = cpu.interpretInstruction(0x8127)
        operation.assertType<SubtractVxFromVy>()

        operation!!.execute(memory)

        assertEquals((-1).toByte(), memory.getV(1))
        assertEquals(3.toByte(), memory.getV(2))
        assertEquals(0.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test shl vx with 1 in most significant bit`() {
        memory.setV(1, 0xFF.toByte())
        val operation = cpu.interpretInstruction(0x810E)
        operation.assertType<ShiftVxLeft>()

        operation!!.execute(memory)

        assertEquals(0xFE.toByte(), memory.getV(1))
        assertEquals(1.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test shl vx with 0 in most significant bit`() {
        memory.setV(1, 0b110)
        val operation = cpu.interpretInstruction(0x810E)
        operation.assertType<ShiftVxLeft>()

        operation!!.execute(memory)

        assertEquals(0b1100.toByte(), memory.getV(1))
        assertEquals(0.toByte(), memory.getV(0xF))
    }


    @Test
    fun `test skip if Vx != Vy is true`() {
        memory.setV(1, 5)
        memory.setV(2, 2)

        val operation = cpu.interpretInstruction(0x9120)
        operation.assertType<SkipIfVxAndVyNotEqual>()

        operation!!.execute(memory)
        assertEquals(2.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if Vx != Vy is not true`() {
        memory.setV(1, 5)
        memory.setV(2, 5)

        val operation = cpu.interpretInstruction(0x9120)
        operation.assertType<SkipIfVxAndVyNotEqual>()

        operation!!.execute(memory)
        assertEquals(0.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test set i`() {
        val operation = cpu.interpretInstruction(0xA333)

        operation.assertType<SetI>()
        operation!!.execute(memory)
        assertEquals(0x333.toUShort(), memory.getI())
    }

    @Test
    fun `test jump v0`() {
        memory.setV(0, 1)
        val operation = cpu.interpretInstruction(0xB333)

        operation.assertType<JumpOffsetV0>()
        operation!!.execute(memory)
        assertEquals(0x334.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if key vx is pressed`() {
        keyboard.key = Chip8Key.A
        memory.setV(0xA, 0xA)
        val operation = cpu.interpretInstruction(0xEA9E)
        operation.assertType<SkipIfKeyVxIsPressed>()

        operation!!.execute(memory)
        assertEquals(2.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if key vx is pressed, but we are not pressing`() {
        keyboard.key = Chip8Key.NONE
        memory.setV(0xA, 0)
        val operation = cpu.interpretInstruction(0xEA9E)
        operation.assertType<SkipIfKeyVxIsPressed>()

        operation!!.execute(memory)
        assertEquals(0.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if key vx is not pressed`() {
        keyboard.key = Chip8Key.A
        memory.setV(0xA, 0)
        val operation = cpu.interpretInstruction(0xEAA1)
        operation.assertType<SkipIfKeyVxIsNotPressed>()

        operation!!.execute(memory)
        assertEquals(2.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test skip if key vx is not pressed, but we are pressing`() {
        keyboard.key = Chip8Key.A
        memory.setV(0xA, 0xA)
        val operation = cpu.interpretInstruction(0xEAA1)
        operation.assertType<SkipIfKeyVxIsNotPressed>()

        operation!!.execute(memory)
        assertEquals(0.toUShort(), memory.getProgramCounter())
    }

    @Test
    fun `test set vx as delay timer`() {
        memory.setDelayTimer(100)
        val operation = cpu.interpretInstruction(0xF207)
        operation.assertType<SetVxToDelayTimer>()

        operation!!.execute(memory)
        assertEquals(100.toByte(), memory.getV(2))
    }

    @Test
    fun `test wait for keypress and store in vx`() {
        keyboard.key = Chip8Key.A
        memory.setDelayTimer(100)
        val operation = cpu.interpretInstruction(0xF20A)
        operation.assertType<WaitForKeyPressAndStoreInVx>()

        operation!!.execute(memory)
        assertEquals(0xA.toByte(), memory.getV(2))
    }

    @Test
    fun `test set delay timer to vx`() {
        memory.setV(2, 55)
        val operation = cpu.interpretInstruction(0xF215)
        operation.assertType<SetDelayTimerToVx>()

        operation!!.execute(memory)
        assertEquals(55.toByte(), memory.getV(2))
    }

    @Test
    fun `test set sound timer to vx`() {
        memory.setV(2, 55)
        val operation = cpu.interpretInstruction(0xF218)
        operation.assertType<SetSoundTimerToVx>()

        operation!!.execute(memory)
        assertEquals(55.toByte(), memory.getSoundTimer())
    }

    @Test
    fun `test add vx to i`() {
        memory.setV(2, 1)
        memory.setI(3.toUShort())
        val operation = cpu.interpretInstruction(0xF21E)
        operation.assertType<AddVxToI>()

        operation!!.execute(memory)
        assertEquals(4.toUShort(), memory.getI())
        assertEquals(0.toByte(), memory.getV(0xF))
    }

    @Test
    fun `test add vx to i, with overflow`() {
        memory.setV(2, 1)
        memory.setI(0xFFFF.toUShort())
        val operation = cpu.interpretInstruction(0xF21E)
        operation.assertType<AddVxToI>()

        operation!!.execute(memory)
        assertEquals(0.toShort(), memory.getI())
        assertEquals(1.toByte(), memory.getV(0xF))
    }
}