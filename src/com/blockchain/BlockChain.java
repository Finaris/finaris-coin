package com.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class which contains the infrastructure of a block mainChain and its associated supporting methods.
 */

public class BlockChain {

    // HashMap of Block hashes to Blocks will be internal structure.
    private Map<String, Block> blocks = new HashMap<>();

    // HashMap of Block hashes to respective indices to keep track of depth of the mainChain.
    private Map<Integer, ArrayList<Block>> depth = new HashMap<>();

    // ArrayList which keeps track of the main mainChain of the block mainChain.
    private ArrayList<Block> mainChain = new ArrayList<>();

    // Initially empty genesisBlock.
    private Block genesisBlock = null;

    public BlockChain() {}

    /** Determines if the new block we wish to add results in a valid block mainChain.
     *  This includes checking for: a valid index, a proper previousHash pointer, and a proper hash.
     *
     * @param newBlock Block which we are adding in reference to previousBlock.
     * @param previousBlock Block which we are adding to.
     * @return Boolean determining if a provided block can be validly added to the block mainChain.
     */
    public boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        // Sanity check that neither blocks are null.
        if (newBlock == null || previousBlock == null)
            throw new IllegalArgumentException();

        /* Check to make sure that the index is one greater, that the previousHash in the newBlock is the same
         * as the hash of the previousBlock, and that a block with the previous hash exists in the mainChain. */
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
     * @param genesisBlock Provided genesisBlock to use in checking the mainChain.
     * @return A boolean value determining if the BlockChain is valid.
     */
    public boolean isValidChain(Block genesisBlock) {
        // First check that the genesis block of the mainChain is valid and the same as the genesis block passed in.
        if (!BlockUtilities.isValidGenesisBlock(this.genesisBlock) || this.genesisBlock != genesisBlock)
            return false;

        // Now, check that each block is properly pointed to from a block in the mainChain.
        boolean shouldHaveGenesis = false;
        for (int i = 1; i < depth.size(); i++) {
            shouldHaveGenesis = true;
            ArrayList<Block> blocksAtIndex = depth.get(i);
            for (Block block: blocksAtIndex) {
                /* Double check that the block mainChain has the block with the previous hash and that the current block
                 * can be a successor to the previous block. */
                if (!(blocks.containsKey(block.getPreviousHash()) &&
                        isValidNewBlock(block, blocks.get(block.getPreviousHash()))))
                    return false;
            }
        }

        // Verify there is at most one genesis block (depending on if there are other blocks).
        return (shouldHaveGenesis && depth.get(0).size() == 1) || (!shouldHaveGenesis && depth.get(0).size() == 0);
    }

    /** Adds a new block to the block chain and updates the main chain if needed.
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
            // Check to make sure that it can properly be added to the mainChain.
            if (!isValidNewBlock(block, blocks.get(block.getPreviousHash())))
                throw new IllegalArgumentException();
        } else {
            throw new IllegalArgumentException();
        }

        updateBlockChain(block);
    }

    /** After validating a block, we need to update all internal data structures and the main chain.
     *
     * @param block Block which will be used to update the block mainChain.
     */
    private void updateBlockChain(Block block) {
        // Update the block internally, as well as either update or add an ArrayList of Blocks at a certain index.
        blocks.put(block.getHash(), block);
        if (depth.containsKey(block.getIndex())) {
            depth.get(block.getIndex()).add(block);
        } else {
            depth.put(block.getIndex(), new ArrayList<Block>(){{
                add(block);
            }});
        }

        /* Update the main mainChain if needed. We add one to the index as a block mainChain with n blocks can only have
         * a maximum index of n-1. */
        if (block.getIndex() + 1 > mainChain.size()) {
            // Create a new ArrayList of Blocks down to the genesis block and update the main mainChain.
            ArrayList<Block> newChain = new ArrayList<>();
            Block iterBlock = block;
            while (iterBlock.getPreviousHash() != null) {
                newChain.add(iterBlock);
                /* There should not be any index errors as all new blocks should be validated (on a valid mainChain)
                 * prior to updateBlockChain() being called. */
                iterBlock = blocks.get(iterBlock.getPreviousHash());
            }
            // Add the genesis block at the end of the iteration, then update the mainChain.
            newChain.add(genesisBlock);
            mainChain = newChain;
        }
    }

    // Getters for genesis block and the main chain.
    public Block getGenesisBlock() {
        return genesisBlock;
    }

    public ArrayList<Block> getMainChain() {
        return mainChain;
    }

}
