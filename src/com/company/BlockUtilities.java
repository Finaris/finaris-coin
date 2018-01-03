package com.company;

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

}
