package ru.alepar.vuzetty.server.api;

import java.math.BigInteger;

public class Hash {

    private final byte[] hash;

    public Hash(byte[] hash) {
        this.hash = hash;
    }

    public Hash(String hash) {
        this.hash = new BigInteger(hash, 16).toByteArray();
    }

    @Override
    public String toString() {
        return new BigInteger(hash).toString(16);
    }

    public byte[] bytes() {
        return hash;
    }

}
