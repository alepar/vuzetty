package ru.alepar.vuzetty.client.play;

import ru.alepar.vuzetty.client.run.CmdRunner;

public class PlayerUrlRunner implements UrlRunner {

    private final CmdRunner cmdRunner;
    private final String playerFile;

    public PlayerUrlRunner(CmdRunner cmdRunner, String playerFile) {
        this.cmdRunner = cmdRunner;
        this.playerFile = playerFile;
    }

    @Override
    public void run(String url) {
        cmdRunner.exec(new String[]{
                playerFile,
                url
        });
    }
}
