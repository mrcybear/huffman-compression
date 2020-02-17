package com.github.mrcybear.huffman;

import java.io.*;

public class HuffmanCoder {

    private static HuffmanCoder instance;

    private HuffmanCoder() {
    }

    public static HuffmanCoder getInstance() {

        if (instance == null) {
            instance = new HuffmanCoder();
        }

        return instance;
    }

    public byte[] encode(int[] symbols) throws HuffmanException {
        try {
            FrequencyTable table = new FrequencyTable();
            for (int symbol : symbols) {
                table.append(symbol);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BitWriter bitOutput = new BitWriter(output);
            DataOutputStream dataOutput = new DataOutputStream(output);

            dataOutput.writeInt(symbols.length);

            StaticTree tree;
            try {
                tree = new StaticTree(table);
            } catch (BitStackFullException ex) {
                throw new HuffmanException("code length overflow");
            }
            byte[] treeAsByteArray = tree.toByteArray();
            dataOutput.writeInt(treeAsByteArray.length);
            dataOutput.write(treeAsByteArray);
            dataOutput.flush();

            for (int symbol : symbols) {
                StaticTree.SymbolCode code = tree.getSymbolCode(symbol);
                bitOutput.writeBits(code.code, code.length);
            }

            bitOutput.flush();

            return output.toByteArray();

        } catch (IOException ex) {
            throw new HuffmanException(ex.getMessage());
        }
    }

    public int[] decode(byte[] data) throws HuffmanException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            DataInputStream dataInput = new DataInputStream(input);

            int symbolNumber = dataInput.readInt();

            int treeAsByteArrayLength = dataInput.readInt();
            byte[] treeAsByteArray = new byte[treeAsByteArrayLength];
            dataInput.readFully(treeAsByteArray);

            StaticTree tree;
            try {
                tree = new StaticTree(treeAsByteArray);
            } catch (BitStackFullException ex) {
                throw new HuffmanException("code length overflow, data may be corrupted");
            }

            BitReader bitInput = new BitReader(input);

            int[] output = new int[symbolNumber];
            for (int i = 0; i < symbolNumber; i++) {
                while (tree.move(bitInput.readBit())) {
                }

                output[i] = tree.getCurrentSymbol();

                tree.returnToRootNode();
            }

            return output;

        } catch (EOFException ex) {
            throw new HuffmanException("unexpected end of file, data corrupted");

        } catch (IOException ex) {
            throw new HuffmanException(ex.getMessage());
        }
    }
}
