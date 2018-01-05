package com.blockchain;

/**
 * Struct-like class which contains definition of a block.
 */

public class Block {

    private int index;
    private long timestamp;
    private String data, hash, previousHash;

    public Block(int index, String data, long timestamp, String hash, String previousHash) {
        this.index = index;
        this.data = data;
        this.timestamp = timestamp;
        this.hash = hash;
        this.previousHash = previousHash;
    }

    // Getters for member variables.
    public int getIndex(){
        return index;
    }

    public String getData(){
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash(){
        return hash;
    }

    public String getPreviousHash(){
        return previousHash;
    }

}
