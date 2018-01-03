package com.company;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlockChainTest {

    @Test
    public void testAdd() {
        // Test the functionality of add() as well as isValidNewBlock() on a BlockChain object.
        BlockChain chain = new BlockChain();

        // Creates genesis block with data "TEST" at timestamp 123456789 seconds and add to chain.
        String genesisHash = BlockUtilities.hashBlock(0, "TEST", 123456789, null);
        Block genesisBlock = new Block(0, "TEST", 123456789, genesisHash, null);
        chain.add(genesisBlock);

        /* Check how validity changes if we modify a valid Block with fields:
         * index: 1, data: "BAR", timestamp: 234567891, previousHash: genesisHash
         * (and with its own hash generated). */
        String nbData = "BAR";
        int nbIndex = 1;
        long nbTS = 234567891;

        Map<Block, Boolean> blocksToCheck = new HashMap<Block, Boolean>(){{
            // Check if block is invalid with index 2 instead of 1.
            String nbHash = BlockUtilities.hashBlock(2, nbData, nbTS, genesisHash);
            put(new Block(2, nbData, nbTS, nbHash, genesisHash), false);

            // Check if block is invalid with an incorrect previous hash.
            nbHash = BlockUtilities.hashBlock(nbIndex, nbData, nbTS, genesisHash);
            put(new Block(nbIndex, nbData, nbTS, nbHash, nbHash), false);

            // Check if block is invalid on the same, corrected block but with a malformed hash.
            put(new Block(nbIndex, nbData, nbTS, nbHash, "null"), false);

            // Check if block is valid on correctly formed Block.
            nbHash = BlockUtilities.hashBlock(1, "BAR", 234567891, genesisHash);
            put(new Block(nbIndex, nbData, nbTS, nbHash, genesisHash), true);
        }};

        // Iterate through blocksToCheck and verify their validity is what is expected.
        for(Map.Entry<Block, Boolean> entry: blocksToCheck.entrySet()) {
            Assert.assertEquals(entry.getValue(), chain.isValidNewBlock(entry.getKey(), genesisBlock));
        }

    }

}