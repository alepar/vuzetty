package ru.alepar.vuzetty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.alepar.vuzetty.client.gui.MonitorTorrent;
import ru.alepar.vuzetty.client.jmx.MonitorLookup;
import ru.alepar.vuzetty.client.jmx.MonitorTorrentMXBean;
import ru.alepar.vuzetty.client.remote.VuzettyClient;

import java.net.InetSocketAddress;

import static ru.alepar.vuzetty.client.classload.MonitorLookupLoader.loadLookup;

public class ClientMain {

    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);
    private static final InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("azureus.alepar.ru", 31337);

    public static void main(String[] args) throws Exception {
        try {
            log.debug("looking for existing monitor");
            final MonitorLookup monitorLookup = loadLookup();
            MonitorTorrentMXBean monitor = monitorLookup.findMonitor();

            if(monitor == null) {
                log.debug("no monitor found, creating new one");
                monitor = new MonitorTorrent(new VuzettyClient(SERVER_ADDRESS));
                monitorLookup.registerMonitor(monitor);
            }

            log.debug("submitting torrent to vuze...");
            monitor.addTorrent(args[0]);
            log.debug("...ok");

        } catch (Exception e) {
            log.error("caught exception", e);
            System.exit(1);
        }
    }

}