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

    @Test
    public void testSetupTestChain() {
        /* Verify by checking the hash of the genesisBlock with its expected values of data: "TEST" and
         * timestamp: 123456789. */
        BlockChain chain = BlockUtilities.setupTestChain();
        Assert.assertEquals(BlockUtilities.hashBlock(0, "TEST", 1, null),
                chain.getGenesisBlock().getHash());
    }

    @Test
    public void testIsValidGenesisBlock() {
        // Create a valid and invalid genesis block, and subsequently verify their validity.
        Block validGenesisBlock = new Block(0, "GOOD", 123, BlockUtilities.hashBlock(0, "GOOD", 123, null), null);
        Block invalidGenesisBlock = new Block(1, "BAD", 123, BlockUtilities.hashBlock(1, "BAD", 123, null), "abc");

        Assert.assertTrue(BlockUtilities.isValidGenesisBlock(validGenesisBlock));
        Assert.assertFalse(BlockUtilities.isValidGenesisBlock(invalidGenesisBlock));
    }

    @Test
    public void testIsValidBlockStructure() {
        /* Create a valid Block with random data and determine if it passes. NOTE: valid structure does not guarantee
         * validated data or hash values. */
        Block validBlock = new Block(54, "DOG", 4567, "abc", "123");
        Assert.assertTrue(BlockUtilities.isValidBlockStructure(validBlock));
    }

}