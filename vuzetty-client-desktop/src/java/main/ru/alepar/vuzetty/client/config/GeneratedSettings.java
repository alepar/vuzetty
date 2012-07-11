package ru.alepar.vuzetty.client.config;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GeneratedSettings implements Settings {

    @Override
    public String getString(String key) {
        if("client.nickname".equals(key)) {
            final SecureRandom random = new SecureRandom();

            final byte[] bytes = new byte[4];
            random.nextBytes(bytes);
            return new BigInteger(bytes).abs().toString(16);
        }
        return null;
    }

}
