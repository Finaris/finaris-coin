package com.company;

import org.junit.Assert;
import org.junit.Test;

public class BlockChainTest {

    @Test
    public void testAdd() {
        // Test the functionality of add() as well as isValidNewBlock() on a BlockChain object.
        BlockChain chain = new BlockChain();

        // Creates genesis block with data "TEST" at timestamp 123456789 seconds and add to chain.
        String genesisHash = BlockUtilities.hashBlock(0, "TEST", 123456789, null);
        Block genesisBlock = new Block(0, "TEST", 123456789, genesisHash, null);
        chain.add(genesisBlock);

        // Check if index is invalid on a block with index 2 and data "BAR" at timestamp 234567891 seconds.
        String newBlockHash = BlockUtilities.hashBlock(2, "BAR", 234567891, genesisHash);
        Block newBlock = new Block(2, "BAR", 234567891, newBlockHash, genesisHash);
        Assert.assertFalse(chain.isValidNewBlock(genesisBlock, newBlock));

        // Check if index is invalid on the same block with a corrected index, but with an incorrect previous hash.
        newBlockHash = BlockUtilities.hashBlock(1, "BAR", 234567891, genesisHash);
        newBlock = new Block(1, "BAR", 234567891, newBlockHash, newBlockHash);
        Assert.assertFalse(chain.isValidNewBlock(genesisBlock, newBlock));

        // Check if index is invalid on the same block with a corrected index, but with a malformed hash.
        newBlock = new Block(1, "BAR", 234567891, newBlockHash, "null");
        Assert.assertFalse(chain.isValidNewBlock(genesisBlock, newBlock));

        // Add a correctly formed newBlock.
        newBlockHash = BlockUtilities.hashBlock(1, "BAR", 234567891, genesisHash);
        newBlock = new Block(1, "BAR", 234567891, newBlockHash, genesisHash);
        chain.add(newBlock);
    }

}