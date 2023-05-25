@file:OptIn(ExperimentalUnsignedTypes::class)

package com.afewroosloose.chip8.utils

import org.junit.Assert.assertTrue
import org.junit.Assert.fail

inline fun <reified T> Any?.assertType() {
    assertTrue("Is instance of type ${T::class.simpleName}", this is T)
}

inline fun Array<UByteArray>.assertAllZero() {
    this.forEach {
        it.forEach {
            if (it != 0x00.toUByte()) {
                fail("We had a non-zero byte")
            }
        }
    }
}