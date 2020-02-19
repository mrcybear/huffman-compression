package com.github.mrcybear.huffman;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FrequencyTableTest {

    FrequencyTable table;

    @Before
    public void before() {
        table = new FrequencyTable();
    }

    @Test
    public void tableIsEmptyAfterCreation() {
        assertSame(0, table.symbolSet().size());
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerExceptionIsThrownIfSymbolDoesNotExist() {
        table.getFrequency(0);
    }

    @Test
    public void frequenciesAreStoredForAllAddedSymbols() {
        for (int symbol = 0; symbol < 3; symbol++) {
            for (int j = 0; j < (symbol + 1); j++) {
                table.append(symbol);
            }
            assertSame(symbol + 1, table.getFrequency(symbol));
        }
    }

    @Test
    public void symbolSetIncludesAllUniqueSymbols() {
        for (int symbol = 0; symbol < 3; symbol++) {
            table.append(symbol);
        }
        Set<Integer> actualSet = table.symbolSet();
        Set<Integer> requiredSet = new TreeSet<>();
        for (int symbol = 0; symbol < 3; symbol++) {
            requiredSet.add(symbol);
        }
        assertTrue(requiredSet.containsAll(actualSet));
    }
}