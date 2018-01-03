package com.company;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class which contains the infrastructure of a block chain and its associated supporting methods.
 */

public class BlockChain {

    // HashMap of Block hashes to Blocks will be internal structure.
    private HashMap<String, Block> chain = new HashMap<>();

    public BlockChain() {}

    /** Determines if the new block we wish to add results in a valid block chain.
     *  This includes checking for: a valid index, a proper previousHash pointer, and a proper hash.
     *
     * @param newBlock Block which we are adding in reference to previousBlock.
     * @param previousBlock Block which we are adding to.
     * @return Boolean determining if a provided block can be validly added to the block chain.
     */
    public boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        /* Check to make sure that the index is one greater and that the previousHash in the newBlock is the same
         * as the hash of the previousBlock. */
        if (previousBlock.getIndex() != newBlock.getIndex() - 1 ||
                !previousBlock.getHash().equals(newBlock.getPreviousHash()))
            return false;

        // Sanity check for a proper hash on the newBlock.
        return BlockUtilities.hashBlock(newBlock.getIndex(), newBlock.getData(), newBlock.getTimestamp(),
                newBlock.getPreviousHash()).equals(newBlock.getHash());
    }

    /** Adds a new block to the block chain.
     *
     * @param block Block to add.
     */
    public void add(Block block) {
        // Define special behavior for a genesis block.
        if (chain.size() == 0 && block.getPreviousHash() == null && block.getIndex() == 0) {
            chain.put(block.getHash(), block);
        } else if (chain.size() > 0) {
            /* First check to make sure that a block with the previous hash exists in the chain. Then, check
             * to make sure that it can properly be added to the chain. */
            if (!chain.containsKey(block.getPreviousHash()) ||
                    !isValidNewBlock(block, chain.get(block.getPreviousHash())))
                throw new MalformedParametersException();
            chain.put(block.getHash(), block);
        } else {
            throw new MalformedParametersException();
        }
    }

}
