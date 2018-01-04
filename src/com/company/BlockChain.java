package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class which contains the infrastructure of a block chain and its associated supporting methods.
 */

public class BlockChain {

    // HashMap of Block hashes to Blocks will be internal structure.
    private Map<String, Block> blocks = new HashMap<>();

    // HashMap of Block hashes to respective indices to keep track of depth of the chain.
    private Map<Integer, ArrayList<Block>> depth = new HashMap<>();

    // Initially empty genesisBlock.
    private Block genesisBlock = null;

    public BlockChain() {}

    /** Determines if the new block we wish to add results in a valid block chain.
     *  This includes checking for: a valid index, a proper previousHash pointer, and a proper hash.
     *
     * @param newBlock Block which we are adding in reference to previousBlock.
     * @param previousBlock Block which we are adding to.
     * @return Boolean determining if a provided block can be validly added to the block chain.
     */
    public boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        /* Check to make sure that the index is one greater, that the previousHash in the newBlock is the same
         * as the hash of the previousBlock, and that a block with the previous hash exists in the chain. */
        if (previousBlock.getIndex() != newBlock.getIndex() - 1 ||
                !previousBlock.getHash().equals(newBlock.getPreviousHash()) ||
                !blocks.containsKey(newBlock.getPreviousHash()))
            return false;

        // Sanity check for a proper hash on the newBlock.
        return BlockUtilities.hashBlock(newBlock.getIndex(), newBlock.getData(), newBlock.getTimestamp(),
                newBlock.getPreviousHash()).equals(newBlock.getHash());
    }

    /** Checks if a BlockChain is valid (this should always be true given the rules for adding, but still good to have).
     *
     * @param genesisBlock Provided genesisBlock to use in checking the chain.
     * @return A boolean value determining if the BlockChain is valid.
     */
    public boolean isValidChain(Block genesisBlock) {
        // First check that the genesis block of the chain is valid and the same as the genesis block passed in.
        if (!BlockUtilities.isValidGenesisBlock(this.genesisBlock) || this.genesisBlock != genesisBlock)
            return false;

        // Now, check that each block is properly pointed to from a block in the chain.
        boolean shouldHaveGenesis = false;
        for (int i = 1; i < depth.size(); i++) {
            shouldHaveGenesis = true;
            ArrayList<Block> blocksAtIndex = depth.get(i);
            for (Block block: blocksAtIndex) {
                /* Double check that the block chain has the block with the previous hash and that the current block
                 * can be a successor to the previous block. */
                if (!(blocks.containsKey(block.getPreviousHash()) &&
                        isValidNewBlock(block, blocks.get(block.getPreviousHash()))))
                    return false;
            }
        }

        // Verify there is at most one genesis block (depending on if there are other blocks).
        return (shouldHaveGenesis && depth.get(0).size() == 1) || (!shouldHaveGenesis && depth.get(0).size() == 0);
    }

    /** Adds a new block to the block chain.
     *
     * @param block Block to add.
     */
    public void add(Block block) {
        // First check that the input block has valid structure.
        if (!BlockUtilities.isValidBlockStructure(block))
            throw new IllegalArgumentException();

        // If the genesisBlock is defined and we have a new genesisBlock, raise an error (as we can only have one).
        if (genesisBlock != null && BlockUtilities.isValidGenesisBlock(block))
            throw new IllegalArgumentException();

        // Define special behavior for a genesis block (including checking its hash).
        if (blocks.size() == 0 && block.getPreviousHash() == null && block.getIndex() == 0 &&
                BlockUtilities.hashBlock(block.getIndex(), block.getData(), block.getTimestamp(), null)
                        .equals(block.getHash())) {
            genesisBlock = block;
        } else if (blocks.size() > 0) {
            // Check to make sure that it can properly be added to the chain.
            if (!isValidNewBlock(block, blocks.get(block.getPreviousHash())))
                throw new IllegalArgumentException();
        } else {
            throw new IllegalArgumentException();
        }

        // Update the block internally, as well as either update or add an ArrayList of Blocks at a certain index.
        blocks.put(block.getHash(), block);
        if (depth.containsKey(block.getIndex())) {
            depth.get(block.getIndex()).add(block);
        } else {
            depth.put(block.getIndex(), new ArrayList<Block>(){{
                add(block);
            }});
        }
    }

    // Getter for genesisBlock.
    public Block getGenesisBlock() {
        if (genesisBlock == null)
            throw new NullPointerException();
        return genesisBlock;
    }

}
