package ru.alepar.vuzetty.client.play;

import java.io.File;

public interface UrlRunner {
    void run(String url);

    public class NativeFactory {

        private static String[] knownPlayers = new String[] {
                "/usr/bin/vlc",
                "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe",
                "C:\\Program Files\\Media Player Classic - Home Cinema\\mpc-hc64.exe"
        };

        public UrlRunner create() {
            for (String player : knownPlayers) {
                if(new File(player).canExecute()) {
                    return new PlayerUrlRunner(new RuntimeCmdRunner(), player);
                }
            }
            throw new RuntimeException("could not find suitable player");
        }
    }

}
