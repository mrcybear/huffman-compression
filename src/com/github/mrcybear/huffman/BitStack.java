package com.github.mrcybear.huffman;

public class BitStack {    
    private int bits;
    private int bitNumber;
    private int mask = 0x01;
    
    public void push(boolean bitSet) throws BitStackFullException {
        if (bitNumber++ == 32) {
            throw new BitStackFullException();
        }
        if (bitSet) {
            bits |= mask;
        }
        mask <<= 1;
    }   
    
    public boolean pop() throws BitStackEmptyException {
        if (bitNumber == 32) {
            mask = 0x80000000;
        } else if (bitNumber > 0) {
            mask >>>= 1;
        } else {
            throw new BitStackEmptyException();
        }
        bitNumber--;
        boolean bit = ((bits & mask) != 0);
        bits &= ~mask; 
        return bit;
    }
    
    public boolean peek() throws BitStackEmptyException {
        if (bitNumber == 0) {
            throw new BitStackEmptyException();
        }
        return ((bits & ((bitNumber == 32) ? 0x80000000 : (mask >>> 1))) != 0);
    }
    
    public int size() {
        return bitNumber;
    }
        
    public int bits() throws BitStackEmptyException {
        if (bitNumber == 0) {
            throw new BitStackEmptyException();
        }
        return bits;
    }
    
    public int bitsViceVersa() throws BitStackEmptyException {
        if (bitNumber == 0) {
            throw new BitStackEmptyException();
        }
        int reversed = 0x00;
        int reversedMask = (0x01 << (bitNumber - 1));
        int originalMask = 0x01;
        for (int i = 0; i < bitNumber; i++) {
            if ((bits & reversedMask) != 0) {
                reversed |= originalMask;
            }
            reversedMask >>>= 1;
            originalMask <<= 1;
        }
        return reversed;
    }
}