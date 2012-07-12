package ru.alepar.vuzetty.client.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class NixCmdResolver implements CmdResolver {

	private final Logger log = LoggerFactory.getLogger(NixCmdResolver.class);

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
        cmdRunner.waitForExit();
		final String resolved = cmdRunner.stdout().trim();
		log.debug("resolved {} to {}", cmd, resolved);
		if(resolved == null || resolved.isEmpty() || !new File(resolved).canExecute()) {
			throw new RuntimeException("failed to resolve " + cmd);
		}
		return resolved;
    }
}
