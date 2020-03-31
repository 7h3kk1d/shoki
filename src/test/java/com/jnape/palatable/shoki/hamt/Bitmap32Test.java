package com.jnape.palatable.shoki.hamt;

import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.shoki.hamt.Bitmap32.Bit.ONE;
import static com.jnape.palatable.shoki.hamt.Bitmap32.Bit.ZERO;
import static com.jnape.palatable.shoki.hamt.Bitmap32.bitmap32;
import static com.jnape.palatable.shoki.hamt.Bitmap32.empty;
import static com.jnape.palatable.shoki.hamt.Bitmap32.full;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Bitmap32Test {

    @Test
    public void fromBits() {
        assertEquals(just(empty()), Bitmap32.fromBits());
        assertEquals(just(empty()), Bitmap32.fromBits(ZERO));

        assertEquals(just(bitmap32(1)), Bitmap32.fromBits(ZERO, ONE));
        assertEquals(just(bitmap32(5)), Bitmap32.fromBits(ONE, ZERO, ONE));
        assertEquals(just(bitmap32(7)), Bitmap32.fromBits(ONE, ONE, ONE));
        assertEquals(just(bitmap32(8)), Bitmap32.fromBits(ONE, ZERO, ZERO, ZERO));

        assertEquals(just(bitmap32(-1)), Bitmap32.fromBits(ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                           ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                           ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                           ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE));

        assertEquals(just(bitmap32(MIN_VALUE)), Bitmap32.fromBits(ONE, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
                                                                  ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
                                                                  ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO,
                                                                  ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO));

        assertEquals(just(bitmap32(MAX_VALUE)), Bitmap32.fromBits(ZERO, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                                  ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                                  ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                                  ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE));

        assertEquals(nothing(), Bitmap32.fromBits(ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                  ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                  ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                  ONE, ONE, ONE, ONE, ONE, ONE, ONE, ONE,
                                                  ZERO));
    }

    @Test
    public void friendlyToString() {
        assertEquals("0b00_00000_00000_00000_00000_00000_00000",
                     bitmap32(0).toString());
        assertEquals("0b00_00000_00000_00000_00000_00000_00001",
                     bitmap32(1).toString());
        assertEquals("0b11_11111_11111_11111_11111_11111_11111",
                     bitmap32(-1).toString());
        assertEquals("0b10_00000_00000_00000_00000_00000_00000",
                     bitmap32(MIN_VALUE).toString());
        assertEquals("0b01_11111_11111_11111_11111_11111_11111",
                     bitmap32(MAX_VALUE).toString());
    }

    @Test
    public void populatedAtIndex() {
        assertFalse(bitmap32(0).populatedAtIndex(0));
        assertTrue(bitmap32(1).populatedAtIndex(0));
        assertFalse(bitmap32(1).populatedAtIndex(1));
        assertTrue(bitmap32(3).populatedAtIndex(1));
        assertFalse(bitmap32(MAX_VALUE).populatedAtIndex(31));
        assertTrue(bitmap32(MIN_VALUE).populatedAtIndex(31));
        assertTrue(bitmap32(-1).populatedAtIndex(31));
    }

    @Test
    public void populateAtIndex() {
        assertEquals(bitmap32(1), bitmap32(0).populateAtIndex(0));
        assertEquals(bitmap32(2), bitmap32(2).populateAtIndex(1));
        assertEquals(bitmap32(3), bitmap32(1).populateAtIndex(1));
        assertEquals(bitmap32(MIN_VALUE), bitmap32(0).populateAtIndex(31));
        assertEquals(bitmap32(-1), bitmap32(MAX_VALUE).populateAtIndex(31));
    }

    @Test
    public void evictAtIndex() {
        assertEquals(bitmap32(0), bitmap32(1).evictAtIndex(0));
        assertEquals(bitmap32(1), bitmap32(1).evictAtIndex(1));
        assertEquals(bitmap32(2), bitmap32(3).evictAtIndex(0));
        assertEquals(bitmap32(MAX_VALUE), bitmap32(-1).evictAtIndex(31));
    }

    @Test
    public void bitsBelow() {
        assertEquals(empty(),
                     empty().lowerBits(10));
        assertEquals(bitmap32(0b00_00000_00000_00000_00000_00000_00000),
                     bitmap32(0b00_00000_00000_00000_00000_00000_00001).lowerBits(0));
        assertEquals(bitmap32(0b00_00000_00000_00000_00000_00000_00001),
                     bitmap32(0b00_00000_00000_00000_00000_00000_00011).lowerBits(1));
        assertEquals(bitmap32(0b00_00000_00000_00000_00000_00000_01111),
                     bitmap32(0b00_00000_00000_00000_00000_00000_11111).lowerBits(4));
        assertEquals(bitmap32(0b11_11111_11111_11111_11111_11111_11111),
                     bitmap32(0b11_11111_11111_11111_11111_11111_11111).lowerBits(32));
        assertEquals(bitmap32(0b11_11111_11111_11111_11111_11111_11111),
                     bitmap32(0b11_11111_11111_11111_11111_11111_11111).lowerBits(34));
    }

    @Test
    public void populationCount() {
        assertEquals(0, empty().populationCount());
        assertEquals(0, bitmap32(0b00_00000_00000_00000_00000_00000_00000).populationCount());
        assertEquals(1, bitmap32(0b10_00000_00000_00000_00000_00000_00000).populationCount());
        assertEquals(2, bitmap32(0b10_00000_00000_00000_00000_00000_00001).populationCount());
        assertEquals(32, bitmap32(-1).populationCount());
    }

    @Test
    public void bitwiseAnd() {
        assertEquals(bitmap32(0b00000), bitmap32(0b00000).and(bitmap32(0b00001)));
        assertEquals(bitmap32(0b00000), bitmap32(0b00001).and(bitmap32(0b00000)));
        assertEquals(bitmap32(0b00001), bitmap32(0b00001).and(bitmap32(0b00001)));
    }

    @Test
    public void signedRightShift() {
        assertEquals(bitmap32(0b00000), bitmap32(0b00000).signedShiftR(0));
        assertEquals(bitmap32(0b00000), bitmap32(0b00000).signedShiftR(1));
        assertEquals(bitmap32(0b00000), bitmap32(0b00001).signedShiftR(1));
        assertEquals(bitmap32(MAX_VALUE), bitmap32(-1).signedShiftR(1));
    }

    @Test
    public void bits() {
        assertEquals(0, bitmap32(0).bits());
        assertEquals(1, bitmap32(1).bits());
        assertEquals(MIN_VALUE, bitmap32(MIN_VALUE).bits());
    }

    @Test
    public void hashCodeEquivalence() {
        assertEquals(empty().hashCode(), bitmap32(0).hashCode());
        assertEquals(full().hashCode(), bitmap32(0b11_11111_11111_11111_11111_11111_11111).hashCode());
        assertNotEquals(empty().hashCode(), full().hashCode());
    }
}