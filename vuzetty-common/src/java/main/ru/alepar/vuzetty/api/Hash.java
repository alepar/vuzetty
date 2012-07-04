package ru.alepar.vuzetty.api;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public class Hash implements Serializable {

    private final byte[] hash;

    public Hash(byte[] hash) {
        this.hash = hash;
    }

    public Hash(String hash) {
        this.hash = new BigInteger(hash, 16).toByteArray();
    }

    public byte[] bytes() {
        return hash;
    }

    @Override
    public String toString() {
        return new BigInteger(hash).toString(16);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hash that = (Hash) o;

        return Arrays.equals(hash, that.hash);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

}
