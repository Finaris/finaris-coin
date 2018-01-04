package com.company;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlockChainTest {

    @Test
    public void testAdd() {
        // Creates a test chain with a single genesisBlock (data: "TEST", timestamp: 123456789).
        BlockChain chain = BlockUtilities.setupTestChain();

        // Get the genesis block and its hash for testing below.
        Block genesisBlock = chain.getGenesisBlock();
        String genesisHash = genesisBlock.getHash();

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

    @Test
    public void testIsValidChain() {
        /* Any chain which is made should always be valid (as we have safe guards in place in add() unless something
         * goes horribly wrong, but it doesn't hurt to have an extra sanity check. */
        BlockChain chain = BlockUtilities.setupTestChain();
        Block genesisBlock = chain.getGenesisBlock();

        // Add a random block which is connected to the genesis block of the test chain.
        chain.add(new Block(1, "BAR", 12345, BlockUtilities.hashBlock(1, "BAR", 12345, genesisBlock.getHash()),
                genesisBlock.getHash()));
        Assert.assertTrue(chain.isValidChain(genesisBlock));
    }

}