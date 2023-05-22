package com.afewroosloose.chip8

import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class MemoryTests {
    lateinit var array: ByteArray

    @Before
    public fun setup() {
        array = javaClass.classLoader.getResourceAsStream("stars.ch8").readAllBytes()
    }

    @Test
    public fun `test rom`() {
        assertTrue("The size of the rom isn't 0", array.isNotEmpty())
    }
}