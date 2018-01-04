package com.company;

/**
 * Contains utility functions for setting up and testing BlockChains.
 */

public class BlockUtilities {

    /** Utility function which calculates the hash of a Block. We add spaces between each segment of data to
     *  reduce the odds of an accidental collision.
     *
     * @param index Index of the block.
     * @param data Data contained in a block.
     * @param timestamp Timestamp at which the block was created.
     * @param previousHash Hash of the previous block.
     * @return String hash of the given block.
     */
    public static String hashBlock(int index, String data, long timestamp, String previousHash) {
        String combinedString = index + " " + data + " " + timestamp + " " + previousHash;
        return Integer.toString(combinedString.hashCode());
    }

    /** Creates a BlockChain with a single genesisBlock (with data "TEST" at timestamp 123456789) for testing.
     *  Usually, the genesis block is hard coded, but whenever using this method to generate a chain to test on
     *  the genesis block can simply be retrieved by calling getGenesisBlock() on the chain.
     *
     * @return A BlockChain with only a genesisBlock.
     */
    public static BlockChain setupTestChain() {
        BlockChain chain = new BlockChain();

        // Creates genesis block with data "TEST" at timestamp 123456789 seconds and add to chain.
        String genesisHash = BlockUtilities.hashBlock(0, "TEST", 1, null);
        Block genesisBlock = new Block(0, "TEST", 1, genesisHash, null);
        chain.add(genesisBlock);

        return chain;
    }

    /** Check is the provided block is a valid genesis block.
     *
     * @param block Block to check.
     * @return Boolean value determining if the structure of the genesis block is valid.
     */
    public static boolean isValidGenesisBlock(Block block) {
        // All genesis blocks have an index of 0 and no previousHash.
        return block.getIndex() == 0 && block.getPreviousHash() == null;
    }

    /** Sanity check which determines if the provided Block has a valid structure.
     *  This verifies that Block's data is not malformed at any point in the chain.
     *
     * @param block Block to check.
     * @return Boolean value determining if the Block has a valid structure.
     */
    public static boolean isValidBlockStructure(Block block) {
        return Integer.class.isInstance(block.getIndex()) &&
                String.class.isInstance(block.getData()) &&
                Long.class.isInstance(block.getTimestamp()) &&
                String.class.isInstance(block.getHash()) &&
                // In the case of a genesis block, there is no previousHash so null is okay too.
                (String.class.isInstance(block.getPreviousHash()) || block.getPreviousHash() == null);
    }

}
