package com.github.mrcybear.huffman;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BitStackTest {

    BitStack stack;

    @Before
    public void beforeTest() {
        stack = new BitStack();
    }

    @Test(expected = BitStackEmptyException.class)
    public void popWhenStackIsEmptyThrowsException() throws BitStackEmptyException {
        stack.pop();
    }

    @Test
    public void popAfterPushReturnsTheSameBit() throws BitStackEmptyException, BitStackFullException {
        stack.push(true);
        assertTrue(stack.pop());
    }

    @Test
    public void stackIsLIFOStructure() throws BitStackEmptyException, BitStackFullException {
        stack.push(true);
        stack.push(false);
        stack.push(true);
        stack.push(false);
        assertFalse(stack.pop());
        assertTrue(stack.pop());
        assertFalse(stack.pop());
        assertTrue(stack.pop());
    }

    @Test(expected = BitStackEmptyException.class)
    public void popWhenStackWasEmptiedThrowsException() throws BitStackFullException, BitStackEmptyException {
        for (int i = 0; i < 3; i++) {
            stack.push(true);
        }
        for (int i = 0; i < 3; i++) {
            stack.pop();
        }
        // Now stack is empty
        stack.pop();
    }

    @Test(expected = BitStackFullException.class)
    public void pushCallWhenStackIsFullThrowsException() throws BitStackFullException {
        for (int i = 0; i < 32; i++) {
            stack.push(false);
        }
        // Now stack is full, we try to push one more time
        stack.push(false);
    }

    @Test
    public void newInstanceHasZeroSize() {
        assertSame(0, stack.size());
    }

    @Test
    public void sizeReturnsCorrectValueAfterPush() throws BitStackFullException {
        stack.push(true);
        assertSame(1, stack.size());
    }

    @Test
    public void pushIncreasesSizeAndPopReducesSize() throws BitStackFullException, BitStackEmptyException {
        stack.push(true);
        assertSame(1, stack.size());
        stack.push(false);
        assertSame(2, stack.size());
        stack.pop();
        assertSame(1, stack.size());
        stack.pop();
        assertSame(0, stack.size());
    }

    @Test
    public void peekDoesNotChangeSize() throws BitStackFullException, BitStackEmptyException {
        stack.push(true);
        stack.peek();
        assertSame(1, stack.size());
    }

    @Test
    public void sizeWhenStackIsFullReturnsCorrectValue() throws BitStackFullException {
        for (int i = 0; i < 32; i++) {
            stack.push(false);
        }
        assertSame(32, stack.size());
    }

    @Test(expected = BitStackEmptyException.class)
    public void peekWhenStackIsEmptyThrowsException() throws BitStackEmptyException {
        stack.peek();
    }

    @Test
    public void peekReturnsTheLastBitInStack() throws BitStackFullException, BitStackEmptyException {
        stack.push(true);
        assertTrue(stack.peek());
        stack.push(false);
        assertFalse(stack.peek());
        stack.push(true);
        assertTrue(stack.peek());
        stack.pop();
        assertFalse(stack.peek());
        stack.pop();
        assertTrue(stack.peek());
    }


    @Test(expected = BitStackEmptyException.class)
    public void peekWhenStackWasEmptiedThrowsException() throws BitStackFullException, BitStackEmptyException {
        for (int i = 0; i < 3; i++) {
            stack.push(true);
        }
        for (int i = 0; i < 3; i++) {
            stack.pop();
        }
        // Now stack is empty
        stack.peek();
    }

    @Test
    public void peekWhenStackIsFullReturnsACorrectValue() throws BitStackFullException, BitStackEmptyException {
        for (int i = 0; i < 31; i++) {
            stack.push(false);
        }
        stack.push(true);
        assertTrue(stack.peek());
    }

    @Test(expected = BitStackEmptyException.class)
    public void bitsWhenStackIsEmptyThrowsException() throws BitStackEmptyException {
        stack.bits();
    }

    @Test
    public void bitsReturnsABitMaskOfTheStackContent() throws BitStackFullException, BitStackEmptyException {
        stack.push(true);
        assertSame(0b1, stack.bits());
        stack.push(false);
        assertSame(0b01, stack.bits());
        stack.push(true);
        assertSame(0b101, stack.bits());
        stack.pop();
        assertSame(0b01, stack.bits());
        stack.pop();
        assertSame(0b1, stack.bits());
    }

    @Test(expected = BitStackEmptyException.class)
    public void bitsWhenStackWasEmptiedThrowsException() throws BitStackFullException, BitStackEmptyException {
        for (int i = 0; i < 3; i++) {
            stack.push(true);
        }
        for (int i = 0; i < 3; i++) {
            stack.pop();
        }
        // Now stack is empty
        stack.bits();
    }

    @Test
    public void bitsWhenStackIsFullReturnsTheStackContent() throws BitStackFullException, BitStackEmptyException {
        for (int i = 0; i < 32; i++) {
            stack.push(true);
        }
        assertSame(0xFFFFFFFF, stack.bits());
    }

    @Test(expected = BitStackEmptyException.class)
    public void bitsViceVersaWhenStackIsEmptyThrowsException() throws BitStackEmptyException {
        stack.bitsViceVersa();
    }
}
