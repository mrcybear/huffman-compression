package com.github.mrcybear.huffman;

import java.io.IOException;
import java.io.OutputStream;

public class BitWriter {
    
    private int writeBitBuffer;
    private int writeBitNumber;
    private int currentBitMask = 0x01;
    
    private OutputStream out;
    
    public BitWriter(OutputStream out) {
        this.out = out;
    }
    
    public void writeBits(int bits, int bitNumber) throws IOException {
        while (bitNumber-- > 0) {
            writeBit((bits & 0x01) > 0);
            bits >>= 1;
        }
    }
    
    public void writeBit(boolean bitSet) throws IOException {
        if (bitSet) {
            writeBitBuffer |= currentBitMask;
        }
        currentBitMask <<= 1;
        if (++writeBitNumber == 8) {
            flushBuffer();
        }
    }
        
    public void flush() throws IOException {
        if (writeBitNumber > 0) {
            flushBuffer();
        }
        out.flush();
    }
    
    public void close() throws IOException {
        try {
            flush();
        } catch (IOException ignored) {
        }
        out.close();
    }
    
    private void flushBuffer() throws IOException {
        out.write(writeBitBuffer);
        writeBitBuffer = 0x00;
        writeBitNumber = 0;
        currentBitMask = 0x01;
    }
}