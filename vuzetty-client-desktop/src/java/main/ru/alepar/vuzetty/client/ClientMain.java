package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.config.Configuration;
import ru.alepar.vuzetty.client.config.ResourceConfiguration;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.client.remote.VuzettyRemote;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        args = cleanupJwsMess(args);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultThreadExceptionHandler());

        final Configuration config = new ResourceConfiguration(ResourceBundle.getBundle("vuzetty-config"));

        log.debug("looking for existing monitor");
        final VuzettyDiscovery discovery = new VuzettyDiscovery();
        try {
            VuzettyRemote vuzetty = discovery.discover();

            boolean shouldExit = false;
            if(vuzetty == null) {
                log.debug("no vuzetty found, creating new one");
                vuzetty = new MonitorTorrent(new VuzettyClient(config.getServerAddress()));
                discovery.announce(vuzetty);
            } else {
                log.debug("already running vuzetty found");
                shouldExit = true;
            }

            log.debug("submitting torrent to vuze...");
            vuzetty.addTorrent(args[0]);
            log.debug("...ok");

            if(shouldExit) {
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("caught exception", e);
            System.exit(1);
        }
    }

    private static String[] cleanupJwsMess(String[] args) {
        final List<String> newArgs = new ArrayList<String>(args.length);
        for (String arg : args) {
            if(!"-open".equals(arg)) {
                newArgs.add(arg);
            }
        }
        return newArgs.toArray(new String[newArgs.size()]);
    }

}
