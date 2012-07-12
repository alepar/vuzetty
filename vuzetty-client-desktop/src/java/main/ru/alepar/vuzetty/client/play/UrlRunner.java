package ru.alepar.vuzetty.client.play;

import ru.alepar.vuzetty.client.run.RuntimeCmdRunner;

import java.io.File;

public interface UrlRunner {

    void run(String url);

    public class NativeFactory {

        private static String[] knownPlayers = new String[] {
                "/usr/bin/vlc",
                "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe",
                "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe",
                "C:\\Program Files\\Media Player Classic - Home Cinema\\mpc-hc64.exe",
        };

        public static UrlRunner create(String serverHost) {
            for (String player : knownPlayers) {
                if(new File(player).canExecute()) {
                    return new AddHostUrlRunner(new PlayerUrlRunner(new RuntimeCmdRunner(), player), serverHost);
                }
            }
            return new DummyUrlRunner();
        }
    }

}
