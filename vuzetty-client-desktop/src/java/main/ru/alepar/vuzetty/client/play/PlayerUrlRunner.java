package ru.alepar.vuzetty.client.play;

public class PlayerUrlRunner implements UrlRunner {

    private final CmdRunner cmdRunner;
    private final String playerFile;

    public PlayerUrlRunner(CmdRunner cmdRunner, String playerFile) {
        this.cmdRunner = cmdRunner;
        this.playerFile = playerFile;
    }

    @Override
    public void run(String url) {
        cmdRunner.run(new String[] {
                playerFile,
                url
        });
    }
}
