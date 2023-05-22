package com.afewroosloose.chip8

open class Chip8Exception(message: String? = "An error occurred", cause: Throwable? = null) : Exception(message, cause)

class InvalidProgramSizeException: Chip8Exception("Your program is not a valid size")

class IllegalRegsiterException(index: Int): Chip8Exception("You're trying to access an invalid register V$index")

class StackOverflowException(): Chip8Exception("The stack has overflowed")