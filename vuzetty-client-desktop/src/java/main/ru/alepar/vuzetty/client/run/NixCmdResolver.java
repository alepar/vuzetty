package ru.alepar.vuzetty.client.run;

public class NixCmdResolver implements CmdResolver {

    private final CmdRunner cmdRunner;

    public NixCmdResolver(CmdRunner cmdRunner) {
        this.cmdRunner = cmdRunner;
    }

    @Override
    public String resolve(String cmd) {
        cmdRunner.exec(new String[]{
                "/usr/bin/which",
                cmd,
                "--skip-alias",
                "--skip-functions"
        });
        if(cmdRunner.waitForExit() != 0) {
            throw new RuntimeException("failed to resolve " + cmd + ": " + cmdRunner.stderr());
        }
        return cmdRunner.stdout();
    }
}
