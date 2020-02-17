package com.github.mrcybear.huffman;

import java.util.*;

public class FrequencyTable {
    
//    private Map<Integer,Node> table = new HashMap<Integer,Node>();
    private Map<Integer,Integer> table = new HashMap<>();
            
    public void append(int symbol) {
//        Node node = table.get(symbol);
//        table.put(symbol, new Node(symbol, (node == null) ? 1 : node.frequency + 1));   
        Integer freq = table.get(symbol);
        table.put(symbol, freq == null ? 1 : freq + 1);
    }
    
    public int getFrequency(int symbol) {
//        return table.get(symbol).frequency;
        return table.get(symbol);
    }
        
//    public Collection<Node> nodeCollection() {
//        return table.values();
//    }
    public Set<Integer> symbolSet() {
        TreeSet<Integer> treeSet = new TreeSet<>(table.keySet());
        return treeSet;
    }
}
