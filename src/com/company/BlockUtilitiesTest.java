package com.company;

import org.junit.Assert;
import org.junit.Test;

public class BlockUtilitiesTest {

    @Test
    public void testHashBlock() {
        // Check the trivial case (data is the string "TEST", timestamp is 123456789, and previous hash is "a0a0").
        String hash = BlockUtilities.hashBlock(0, "TEST", 123456789, "a0a0");
        Assert.assertEquals(hash, Integer.toString("0 TEST 123456789 a0a0".hashCode()));

        // Check hashing on a null previous hash (i.e. in the genesis block).
        hash = BlockUtilities.hashBlock(0, "TEST", 123456789, null);
        Assert.assertEquals(hash, Integer.toString("0 TEST 123456789 null".hashCode()));
    }

}