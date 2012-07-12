package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.config.Configuration;
import ru.alepar.vuzetty.client.config.ConfigurationFactory;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.os.OsInteractionFactory;
import ru.alepar.vuzetty.client.remote.VuzettyClient;
import ru.alepar.vuzetty.client.remote.VuzettyRemote;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws Exception {
        args = cleanupJwsMess(args);

        prepStuff();
        final Configuration config = prepConfig();
        prepAssociations(config);

        log.debug("looking for existing monitor");
        final VuzettyDiscovery discovery = new VuzettyDiscovery();
        try {
            VuzettyRemote vuzetty = discovery.discover();

            boolean shouldExit = false;
            if(vuzetty == null) {
                log.debug("no vuzetty found, creating new one");
                vuzetty = new MonitorTorrent(config, new VuzettyClient(config.getServerAddress()));
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

    private static void prepAssociations(Configuration config) {
        final OsInteractionFactory interactionFactory = OsInteractionFactory.Native.create();
        if (config.associateWithMagnetLinks()) {
            interactionFactory.getAssociator().associateWithMagnetLinks();
        }
        if (config.associateWithTorrentFiles()) {
            interactionFactory.getAssociator().associateWithTorrentFiles();
        }
    }

    private static Configuration prepConfig() {
        ConfigurationFactory.askForUserInputIfNeeded();
        return ConfigurationFactory.makeConfiguration();
    }

    private static void prepStuff() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Thread.setDefaultUncaughtExceptionHandler(new DefaultThreadExceptionHandler());
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
