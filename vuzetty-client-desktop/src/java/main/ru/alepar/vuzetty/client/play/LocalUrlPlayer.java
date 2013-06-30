package ru.alepar.vuzetty.client.play;

import ru.alepar.vuzetty.client.run.CmdRunner;

public class LocalUrlPlayer implements UrlPlayer {

    private final CmdRunner cmdRunner;
    private final String playerFile;

    public LocalUrlPlayer(CmdRunner cmdRunner, String playerFile) {
        this.cmdRunner = cmdRunner;
        this.playerFile = playerFile;
    }

    @Override
    public void play(String url) {
        cmdRunner.exec(new String[]{
                playerFile,
                url
        });
    }

    @Override
    public String toString() {
        return "LocalPlayer{" +
                "file='" + playerFile + '\'' +
                '}';
    }
}
