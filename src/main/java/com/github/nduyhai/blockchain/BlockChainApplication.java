package com.github.nduyhai.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BlockChainApplication {
    public static void main(String[] args) {
        final BlockChain chain = new BlockChain();
        System.out.println("Mining block 1...");
        chain.add(new Block(1l, LocalDateTime.now(), "{Amount: 4}", Optional.empty()));
        System.out.println("Mining block 2...");
        chain.add(new Block(2l, LocalDateTime.now(), "{Amount: 4}", Optional.empty()));

        System.out.println("This chain is isValid? " + chain.isValid());
    }
}

class Block {
    private Long index;
    private LocalDateTime timestamp;
    private String data;
    private String previousHash;
    private String hash;
    private Long nonce;

    public Block(Long index, LocalDateTime timestamp, String data, Optional<String> previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash.orElse("");
        this.hash = this.calculateHash();
        this.nonce = 0l;
    }

    public String calculateHash() {
        try {
            final String original = String.join("", String.valueOf(this.index), this.previousHash, this.timestamp.toString(), this.data, String.valueOf(this.nonce));
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(original.getBytes(StandardCharsets.UTF_8));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return "";
        }
 }

    public void mine(Integer difficulty) {
        do {
            this.nonce++;
            this.hash = this.calculateHash();
        }
        while (!this.passed(this.hash, difficulty));
    }

    private boolean passed(String hash, Integer difficulty) {
        //TODO: Implement algorithm test difficulty
        return true;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

}


class BlockChain {
    private final List<Block> chain;

    private final Integer difficulty;

    public BlockChain() {
        chain = new LinkedList<>();
        difficulty = 2;
        chain.add(this.genesis());
    }

    private Block genesis() {
        return new Block(0l, LocalDateTime.of(2018, 1, 1, 0, 0, 0), "Genesis block", Optional.empty());
    }

    public Block latest() {
        return this.chain.get(this.chain.size() - 1);
    }

    public void add(Block newBlock) {
        newBlock.setPreviousHash(this.latest().getHash());
        newBlock.mine(difficulty);
        chain.add(newBlock);
    }

    public boolean isValid() {
        Block previousBlock = chain.get(0);

        for (int i = 1; i < chain.size(); i++) {
            final Block currentBlock = chain.get(i);
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            if (!currentBlock.calculateHash().equals(currentBlock.getHash())) {
                return false;
            }
            previousBlock = currentBlock;
        }
        return true;
    }

}
