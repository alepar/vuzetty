package ru.alepar.vuzetty.client.config;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

public class GeneratedSettings implements Settings {

    public static String[] knownPlayers = new String[]{
            "/usr/bin/vlc",
            "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe",
            "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe"
    };

    @Override
    public String getString(String key) {
        if ("client.nickname".equals(key)) {
            final SecureRandom random = new SecureRandom();

            final byte[] bytes = new byte[4];
            random.nextBytes(bytes);
            return new BigInteger(bytes).abs().toString(16);
        }
        if ("player.video".equals(key)) {
            for (String player : GeneratedSettings.knownPlayers) {
                if (new File(player).canExecute()) {
                    return player;
                }
            }
        }

        return null;
    }

}

