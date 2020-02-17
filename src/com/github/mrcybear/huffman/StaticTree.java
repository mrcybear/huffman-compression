package com.github.mrcybear.huffman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class StaticTree {
        
    private final Node rootNode;
    private Node currentNode;
    private Map<Integer,SymbolCode> huffmanCode;
    
    public StaticTree(FrequencyTable table) throws BitStackFullException {
        rootNode = StaticTree.buildTree(table);
        finishInit();
    }
    
    public StaticTree(byte[] treeAsByteArray) throws IOException, BitStackFullException {
        rootNode = StaticTree.buildTree(treeAsByteArray);
        finishInit();
    }
    
    private void finishInit() throws BitStackFullException {
        currentNode = rootNode;
        BitStack bitStack = new BitStack();
        huffmanCode = new HashMap<>();
        StaticTree.buildHuffmanCode(rootNode, bitStack, huffmanCode);
    }
    
//    private static void insertWithOrderKeeping(Node node, ArrayList<Node> list) {
//        int i = 0;
//        while (i < list.size() && node.frequency > list.get(i).frequency) {
//            i++;
//        }
//        list.add(i, node);
//    }
    
    private static Node buildTree(FrequencyTable table) {
//        ArrayList<Node> sortedNodeList = new ArrayList<Node>();
//        sortedNodeList.addAll(table.nodeCollection());
//        Collections.sort(sortedNodeList);
//        
//        while (sortedNodeList.size() > 1) {
//            Node first = sortedNodeList.remove(0);
//            Node second = sortedNodeList.remove(0);
//
//            Node newNode = new Node(0x00, first.frequency + second.frequency,
//                    first, second);
//                        
//            insertWithOrderKeeping(newNode, sortedNodeList);    
//        }
//                
//        return sortedNodeList.get(0);
               
        Queue<Node> symbolQueue = new PriorityQueue<>();
        Set<Integer> symbols = table.symbolSet();
        for (Integer symbol : symbols) {
            symbolQueue.add(new Node(symbol, table.getFrequency(symbol)));
        }
        
        if (symbolQueue.size() == 1) {
            symbolQueue.add(new Node(-symbolQueue.peek().frequency, 0));
        }
                        
        while (symbolQueue.size() > 1) {
            Node first = symbolQueue.remove();
            Node second = symbolQueue.remove();
            
            symbolQueue.add(new Node(0x00, first.frequency + second.frequency,
                               first, second));
        }
                
        return symbolQueue.remove();
    }
    
    private static Node buildTree(byte[] serializedTree) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(serializedTree);
        BitReader bitReader = new BitReader(input);
        
        try {
            return decodeNode(bitReader);
        } finally {
            bitReader.close();
        }
    }
    
    private static Node decodeNode(BitReader reader) throws IOException {
        if (reader.readBit()) {
            boolean negative = reader.readBit();
            int symbol = reader.readBits(12);
            if (negative) {
                symbol = -symbol;
            }
            return new Node(symbol, 0, null, null);
        } else {
            Node leftChild = decodeNode(reader);
            Node rightChild = decodeNode(reader);
            return new Node(0, 0, leftChild, rightChild);
        }
    }
        
    private static void buildHuffmanCode(Node currentNode, BitStack bitStack,
            Map<Integer,SymbolCode> codeBook) throws BitStackFullException {
        if (currentNode.leftChild == null && currentNode.rightChild == null) {
            SymbolCode code;
            try {
                code = new SymbolCode(bitStack.bits(), bitStack.size());
            } catch (BitStackEmptyException ex) {
                throw new RuntimeException(ex);
            }
            codeBook.put(currentNode.symbol, code);
        } else {
            bitStack.push(false);
            buildHuffmanCode(currentNode.leftChild, bitStack, codeBook);
            try {
                bitStack.pop();
            } catch (BitStackEmptyException ex) {
                throw new RuntimeException(ex);
            }
           
            bitStack.push(true);
            buildHuffmanCode(currentNode.rightChild, bitStack, codeBook);
            try {
                bitStack.pop();
            } catch (BitStackEmptyException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
        
    public SymbolCode getSymbolCode(int symbol) {
        return huffmanCode.get(symbol);
    }
    
    public boolean move(boolean bitSet) {
        currentNode = bitSet ? currentNode.rightChild : currentNode.leftChild;
        return !Node.isLeaf(currentNode);
    }
    
    public int getCurrentSymbol() {
        if (!Node.isLeaf(currentNode)) {
            throw new RuntimeException("current node is not a leaf");
        }
        return currentNode.symbol;
    }
    
    public void returnToRootNode() {
        currentNode = rootNode;
    }
    
    public byte[] toByteArray() {
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BitWriter bitWriter = new BitWriter(output);
   
        try {
            try {
                encodeNode(rootNode, bitWriter);
            } finally {
                bitWriter.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
                
        return output.toByteArray();
    }
    
    private void encodeNode(Node node, BitWriter writer) throws IOException {
        if (Node.isLeaf(node)) {
            writer.writeBit(true);
            if (node.symbol < 0) {
                writer.writeBit(true);
            } else {
                writer.writeBit(false);
            }
            writer.writeBits(Math.abs(node.symbol), 12);
        } else {
            writer.writeBit(false);
            encodeNode(node.leftChild, writer);
            encodeNode(node.rightChild, writer);
        }
    }
    
    public static class SymbolCode {
        
        public final int code;
        public final int length;

        public SymbolCode(int code, int length) {
            this.code = code;
            this.length = length;
        }
    }
            
    public static class Node implements Comparable<Node> {
        
        public final int symbol;
        public final int frequency;
        public final Node leftChild;
        public final Node rightChild;
               
        public Node(int symbol, int frequency) {
            this(symbol, frequency, null, null);
        }

        public Node(int symbol, int frequency, Node leftChild, Node rightChild) {
            this.symbol = symbol;
            this.frequency = frequency;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        @Override
        public int compareTo(Node toCompare) {
            return (this.frequency - toCompare.frequency);
        }
        
        public static boolean isLeaf(Node node) {
            return node.leftChild == null && node.rightChild == null;
        }
    }
}
