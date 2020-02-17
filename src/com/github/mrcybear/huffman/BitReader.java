package com.github.mrcybear.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitReader {
    
    private int readBitsBuffer;
    private int readBitNumber;
    
    private InputStream in;
    
    public BitReader(InputStream in) {
        this.in = in;
    }
    
    public int readBits(int bitNumber) throws IOException {
        int bitsRead = 0x00;
        int currentBitMask = 0x01;
        while (bitNumber-- > 0) {
            if (readBit()) {
                bitsRead |= currentBitMask;
            }
            currentBitMask <<= 1;
        }
        return bitsRead;
    }
    
    public boolean readBit() throws IOException {
        if (readBitNumber == 0) {
            readBitsBuffer = in.read();
            if (readBitsBuffer == -1) {
                throw new EOFException();
            }
            readBitNumber = 8;
        }
        boolean bit = ((readBitsBuffer & 0x01) > 0);
        readBitsBuffer >>= 1;
        readBitNumber--;
        return bit;
    }
    
    public void close() throws IOException {
        in.close();
    }
}
