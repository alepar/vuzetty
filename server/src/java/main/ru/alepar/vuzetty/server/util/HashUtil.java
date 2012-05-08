package ru.alepar.vuzetty.server.util;

import java.math.BigInteger;

public class HashUtil {
    public static String hashToString(byte[] hash) {
        return new BigInteger(hash).toString(16);
    }

    public static byte[] stringToHash(String hash) {
        return new BigInteger(hash, 16).toByteArray();
    }
}
