package com.company;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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

        ArrayList<TestTuple> blocksToCheck = new ArrayList<TestTuple>(){{
            // Check if block is invalid with index 2 instead of 1.
            String nbHash = BlockUtilities.hashBlock(2, nbData, nbTS, genesisHash);
            add(new TestTuple(false, new Block(2, nbData, nbTS, nbHash, genesisHash)));

            // Check if block is invalid with an incorrect previous hash.
            nbHash = BlockUtilities.hashBlock(nbIndex, nbData, nbTS, genesisHash);
            add(new TestTuple(false, new Block(nbIndex, nbData, nbTS, nbHash, nbHash)));

            // Check if block is invalid on the same, corrected block but with a malformed hash.
            add(new TestTuple(false, new Block(nbIndex, nbData, nbTS, nbHash, "null")));

            // Check if block is valid on correctly formed Block.
            nbHash = BlockUtilities.hashBlock(1, "BAR", 234567891, genesisHash);
            add(new TestTuple(true, new Block(nbIndex, nbData, nbTS, nbHash, genesisHash)));
        }};

        // Iterate through blocksToCheck and verify their validity is what is expected.
        for(TestTuple tuple: blocksToCheck) {
            Assert.assertEquals(tuple.getExpected(), chain.isValidNewBlock((Block) tuple.getToCheck(), genesisBlock));
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

    @Test
    public void testGetMainChain() {
        // Done to test updates on the main chain of the block chain.
        BlockChain chain = BlockUtilities.setupTestChain();
        Block genesisBlock = chain.getGenesisBlock();
        Assert.assertEquals(1, chain.getMainChain().size());

        // Add various blocks at different indices and check how the chain updates.
        ArrayList<TestTuple> blocksToCheck = new ArrayList<TestTuple>(){{
            // Add a single block at index 1 and check if the main chain updates to size 2.
            String fooHash = BlockUtilities.hashBlock(1, "FOO", 12345, genesisBlock.getHash());
            add(new TestTuple(2, new Block(1, "FOO", 12345, fooHash, genesisBlock.getHash())));

            // Add a new block with index 1 - this should not change the max chain.
            String barHash = BlockUtilities.hashBlock(1, "BAR", 12346, genesisBlock.getHash());
            add(new TestTuple(2, new Block(1, "BAR", 12346, barHash, genesisBlock.getHash())));

            // Add one new block which connect to Block "FOO." This should update the chain.
            add(new TestTuple(3, new Block(2, "FOO-1", 12347, BlockUtilities.hashBlock(2, "FOO-1", 12347, fooHash),
                    fooHash)));

            // Add a new block which doesn't change the chain, then a new block to that one which should change it.
            String barOneHash = BlockUtilities.hashBlock(2, "BAR-1", 12348, barHash);
            add(new TestTuple(3, new Block(2, "BAR-1", 12348, barOneHash, barHash)));
            add(new TestTuple(4, new Block(3, "BAR-2", 12349, BlockUtilities.hashBlock(3, "BAR-2", 12349, barOneHash),
                    barOneHash)));
        }};

        /* Iterate through blocksToCheck and verify the chain updates properly. Depending on if the chain size changes,
         * determine if the head is what we expect (new or the same as prior) and if the tail is the genesis block. */
        Block mainChainHead = null;
        for(TestTuple tuple: blocksToCheck) {
            // Retrieve the size of the chain prior to adding the block then add the new block to the chain.
            int preChainSize = chain.getMainChain().size();
            chain.add((Block)tuple.getToCheck());

            // Check if the head is what we expect, and if the tail is the genesis block.
            Assert.assertEquals(tuple.getExpected(), chain.getMainChain().size());
            if (chain.getMainChain().size() > preChainSize) {
                Assert.assertEquals(tuple.getToCheck(), chain.getMainChain().get(0));
                mainChainHead = chain.getMainChain().get(0);
            } else {
                Assert.assertEquals(mainChainHead, chain.getMainChain().get(0));
            }
            Assert.assertEquals(genesisBlock, chain.getMainChain().get(chain.getMainChain().size() - 1));
        }

    }

}