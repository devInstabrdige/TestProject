package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.collections.subtract

class CalculatorTest {
    private val calculator = Calculator()

    @Test
    fun testAdd() {
        assertEquals(4, calculator.add(2, 2))
    }

    @Test
    fun testSubtract() {
        assertEquals(0, calculator.subtract(2, 2))
    }
}